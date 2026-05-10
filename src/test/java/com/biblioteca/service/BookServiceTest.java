package com.biblioteca.service;

import com.biblioteca.dto.BookDTO;
import com.biblioteca.exception.LivroNaoEncontradoException;
import com.biblioteca.exception.ValidationException;
import com.biblioteca.model.Book;
import com.biblioteca.model.StatusLeitura;
import com.biblioteca.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do BookService com mocks.
 * Conforme RNF-01 seção 5.2 — testes rápidos sem container.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookValidator bookValidator;

    @InjectMocks
    private BookService bookService;

    private static final String USER_ID = "user-123";
    private static final String OTHER_USER_ID = "user-456";
    private static final String BOOK_ID = "book-abc";

    @Nested
    @DisplayName("criarLivro")
    class CriarLivro {

        @Test
        @DisplayName("RF-04: Deve criar livro com dados válidos")
        void deveCriarLivroComDadosValidos() {
            BookDTO dto = new BookDTO("Dom Casmurro", "Machado de Assis", "978-85-7232-123-4");
            dto.setGenero("Romance");
            dto.setAnoPublicacao(1899);
            dto.setStatusLeitura(StatusLeitura.LIDO);

            when(bookRepository.save(any(Book.class))).thenAnswer(inv -> {
                Book b = inv.getArgument(0);
                b.setId(BOOK_ID);
                return b;
            });

            Book resultado = bookService.criarLivro(dto, USER_ID);

            assertThat(resultado.getId()).isEqualTo(BOOK_ID);
            assertThat(resultado.getUserId()).isEqualTo(USER_ID);
            assertThat(resultado.getTitulo()).isEqualTo("Dom Casmurro");
            assertThat(resultado.getStatusLeitura()).isEqualTo(StatusLeitura.LIDO);
            verify(bookValidator).validar(dto);
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        @DisplayName("RF-04 RN-05: Deve usar QUERO_LER como status padrão quando não informado")
        void deveUsarStatusPadraoQuandoNaoInformado() {
            BookDTO dto = new BookDTO("Teste", "Autor Teste", null);
            // statusLeitura é null

            when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

            Book resultado = bookService.criarLivro(dto, USER_ID);

            assertThat(resultado.getStatusLeitura()).isEqualTo(StatusLeitura.QUERO_LER);
        }

        @Test
        @DisplayName("RF-04: Deve definir dataCadastro e dataAtualizacao na criação")
        void deveDefinirDatasNaCriacao() {
            BookDTO dto = new BookDTO("Teste", "Autor Teste", null);
            when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

            Book resultado = bookService.criarLivro(dto, USER_ID);

            assertThat(resultado.getDataCadastro()).isNotNull();
            assertThat(resultado.getDataAtualizacao()).isNotNull();
        }

        @Test
        @DisplayName("RF-11: Deve lançar exceção para dados inválidos")
        void deveLancarExcecaoParaDadosInvalidos() {
            BookDTO dto = new BookDTO();
            doThrow(new ValidationException(List.of("Título é obrigatório")))
                .when(bookValidator).validar(dto);

            assertThatThrownBy(() -> bookService.criarLivro(dto, USER_ID))
                .isInstanceOf(ValidationException.class);
            verify(bookRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("RF-09: Deve retornar livro do próprio usuário")
        void deveRetornarLivroDoProprioUsuario() {
            Book book = criarBook(BOOK_ID, USER_ID, "Teste");
            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));

            Book resultado = bookService.buscarPorId(BOOK_ID, USER_ID);

            assertThat(resultado.getTitulo()).isEqualTo("Teste");
        }

        @Test
        @DisplayName("RF-09: Deve lançar LivroNaoEncontradoException quando livro não existe")
        void deveLancarExcecaoQuandoLivroNaoExiste() {
            when(bookRepository.findById("inexistente")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.buscarPorId("inexistente", USER_ID))
                .isInstanceOf(LivroNaoEncontradoException.class)
                .hasMessage("Livro não encontrado");
        }

        @Test
        @DisplayName("RNF-05: Deve lançar AccessDeniedException para livro de outro usuário")
        void deveLancarAccessDeniedParaOutroUsuario() {
            Book book = criarBook(BOOK_ID, OTHER_USER_ID, "Teste");
            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));

            assertThatThrownBy(() -> bookService.buscarPorId(BOOK_ID, USER_ID))
                .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("atualizarLivro")
    class AtualizarLivro {

        @Test
        @DisplayName("RF-07: Deve atualizar livro com dados válidos")
        void deveAtualizarLivroComDadosValidos() {
            Book bookExistente = criarBook(BOOK_ID, USER_ID, "Título Antigo");
            bookExistente.setDataCadastro(LocalDateTime.now().minusDays(10));
            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(bookExistente));
            when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

            BookDTO dto = new BookDTO("Título Novo", "Autor Novo", null);
            dto.setStatusLeitura(StatusLeitura.LENDO);

            Book resultado = bookService.atualizarLivro(BOOK_ID, dto, USER_ID);

            assertThat(resultado.getTitulo()).isEqualTo("Título Novo");
            assertThat(resultado.getStatusLeitura()).isEqualTo(StatusLeitura.LENDO);
        }

        @Test
        @DisplayName("RF-07 RN-02: Deve atualizar dataAtualizacao e manter dataCadastro")
        void deveAtualizarDataAtualizacaoEManterDataCadastro() {
            LocalDateTime dataCadastroOriginal = LocalDateTime.now().minusDays(30);
            Book bookExistente = criarBook(BOOK_ID, USER_ID, "Teste");
            bookExistente.setDataCadastro(dataCadastroOriginal);
            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(bookExistente));
            when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

            BookDTO dto = new BookDTO("Teste Atualizado", "Autor", null);
            Book resultado = bookService.atualizarLivro(BOOK_ID, dto, USER_ID);

            assertThat(resultado.getDataCadastro()).isEqualTo(dataCadastroOriginal);
            assertThat(resultado.getDataAtualizacao()).isAfter(dataCadastroOriginal);
        }
    }

    @Nested
    @DisplayName("excluirLivro")
    class ExcluirLivro {

        @Test
        @DisplayName("RF-08: Deve excluir livro do próprio usuário")
        void deveExcluirLivroDoProprioUsuario() {
            Book book = criarBook(BOOK_ID, USER_ID, "Teste");
            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));

            bookService.excluirLivro(BOOK_ID, USER_ID);

            verify(bookRepository).deleteById(BOOK_ID);
        }

        @Test
        @DisplayName("RF-08 RNF-05: Deve negar exclusão de livro de outro usuário")
        void deveNegarExclusaoDeOutroUsuario() {
            Book book = criarBook(BOOK_ID, OTHER_USER_ID, "Teste");
            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));

            assertThatThrownBy(() -> bookService.excluirLivro(BOOK_ID, USER_ID))
                .isInstanceOf(AccessDeniedException.class);
            verify(bookRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("listarLivros e buscarLivros")
    class ListarEBuscar {

        @Test
        @DisplayName("RF-05: Deve listar livros do usuário")
        void deveListarLivrosDoUsuario() {
            List<Book> livros = List.of(
                criarBook("1", USER_ID, "Livro 1"),
                criarBook("2", USER_ID, "Livro 2")
            );
            when(bookRepository.findByUserIdOrderByDataCadastroDesc(USER_ID)).thenReturn(livros);

            List<Book> resultado = bookService.listarLivros(USER_ID);

            assertThat(resultado).hasSize(2);
        }

        @Test
        @DisplayName("RF-06 CA-07: Query vazia deve retornar todos os livros")
        void queryVaziaDeveRetornarTodos() {
            List<Book> livros = List.of(criarBook("1", USER_ID, "Livro 1"));
            when(bookRepository.findByUserIdOrderByDataCadastroDesc(USER_ID)).thenReturn(livros);

            List<Book> resultado = bookService.buscarLivros("", USER_ID);

            assertThat(resultado).hasSize(1);
            verify(bookRepository).findByUserIdOrderByDataCadastroDesc(USER_ID);
            verify(bookRepository, never()).searchByQuery(any(), any());
        }

        @Test
        @DisplayName("RF-06: Query não vazia deve usar searchByQuery")
        void queryNaoVaziaDeveUsarSearch() {
            when(bookRepository.searchByQuery("dom", USER_ID)).thenReturn(List.of());

            bookService.buscarLivros("dom", USER_ID);

            verify(bookRepository).searchByQuery("dom", USER_ID);
        }
    }

    private Book criarBook(String id, String userId, String titulo) {
        Book book = new Book(titulo, "Autor Padrão", null);
        book.setId(id);
        book.setUserId(userId);
        book.setStatusLeitura(StatusLeitura.QUERO_LER);
        book.setDataCadastro(LocalDateTime.now());
        book.setDataAtualizacao(LocalDateTime.now());
        return book;
    }
}
