package com.biblioteca.controller;

import com.biblioteca.dto.BookDTO;
import com.biblioteca.exception.ValidationException;
import com.biblioteca.model.Book;
import com.biblioteca.model.StatusLeitura;
import com.biblioteca.model.User;
import com.biblioteca.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes do BookController com MockMvc standalone.
 * Conforme RF-04 a RF-09.
 */
@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
            .setCustomArgumentResolvers(new TestUserArgumentResolver())
            .build();
    }

    @Test
    @DisplayName("RF-05: GET /books deve listar livros do usuário")
    void deveListarLivros() throws Exception {
        Book book = criarBook("1", "Dom Casmurro", "Machado de Assis");
        when(bookService.listarLivros("user-123")).thenReturn(List.of(book));

        mockMvc.perform(get("/books"))
            .andExpect(status().isOk())
            .andExpect(view().name("books/list"))
            .andExpect(model().attributeExists("books"));
    }

    @Test
    @DisplayName("RF-06: GET /books?q=dom deve buscar livros")
    void deveBuscarLivros() throws Exception {
        when(bookService.buscarLivros("dom", "user-123")).thenReturn(List.of());

        mockMvc.perform(get("/books").param("q", "dom"))
            .andExpect(status().isOk())
            .andExpect(view().name("books/list"))
            .andExpect(model().attribute("query", "dom"));
    }

    @Test
    @DisplayName("RF-04: GET /books/new deve exibir formulário")
    void deveExibirFormularioNovo() throws Exception {
        mockMvc.perform(get("/books/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("books/form"))
            .andExpect(model().attributeExists("book", "statusOptions"));
    }

    @Test
    @DisplayName("RF-09: GET /books/{id} deve exibir detalhes")
    void deveExibirDetalhes() throws Exception {
        Book book = criarBook("abc", "1984", "George Orwell");
        when(bookService.buscarPorId("abc", "user-123")).thenReturn(book);

        mockMvc.perform(get("/books/abc"))
            .andExpect(status().isOk())
            .andExpect(view().name("books/detail"))
            .andExpect(model().attributeExists("book"));
    }

    @Test
    @DisplayName("RF-04: POST /books deve criar livro e redirecionar")
    void deveCriarLivroERedirecionar() throws Exception {
        Book book = criarBook("new-id", "Novo Livro", "Autor");
        when(bookService.criarLivro(any(), eq("user-123"))).thenReturn(book);

        mockMvc.perform(post("/books")
                .param("titulo", "Novo Livro")
                .param("autor", "Autor"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/books"));
    }

    @Test
    @DisplayName("RF-08: POST /books/{id}/delete deve excluir e redirecionar")
    void deveExcluirERedirecionar() throws Exception {
        mockMvc.perform(post("/books/abc/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/books"));
    }

    @Test
    @DisplayName("RF-07: POST /books/{id}/edit deve atualizar e redirecionar")
    void deveAtualizarERedirecionar() throws Exception {
        Book book = criarBook("abc", "Título Atualizado", "Autor");
        when(bookService.atualizarLivro(eq("abc"), any(), eq("user-123"))).thenReturn(book);

        mockMvc.perform(post("/books/abc/edit")
                .param("titulo", "Título Atualizado")
                .param("autor", "Autor"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/books"));
    }

    @Test
    @DisplayName("RF-11: POST /books com validação inválida deve exibir erros no form")
    void deveMostrarErrosValidacao() throws Exception {
        when(bookService.criarLivro(any(), eq("user-123")))
            .thenThrow(new ValidationException(List.of("Título é obrigatório")));

        mockMvc.perform(post("/books")
                .param("titulo", "")
                .param("autor", ""))
            .andExpect(status().isOk())
            .andExpect(view().name("books/form"))
            .andExpect(model().attributeExists("erros"));
    }

    private Book criarBook(String id, String titulo, String autor) {
        Book b = new Book(titulo, autor, null);
        b.setId(id);
        b.setUserId("user-123");
        b.setStatusLeitura(StatusLeitura.QUERO_LER);
        b.setDataCadastro(LocalDateTime.now());
        b.setDataAtualizacao(LocalDateTime.now());
        return b;
    }
}
