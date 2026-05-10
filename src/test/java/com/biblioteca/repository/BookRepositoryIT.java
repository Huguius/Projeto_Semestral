package com.biblioteca.repository;

import com.biblioteca.AbstractIntegrationTest;
import com.biblioteca.model.Book;
import com.biblioteca.model.StatusLeitura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração do BookRepository com MongoDB real via Testcontainers.
 * Valida queries customizadas e isolamento por userId.
 * Conforme RNF-01 seção 5.1.
 */
class BookRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    private static final String USER_A = "user-a-id";
    private static final String USER_B = "user-b-id";

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();

        // Livros do Usuário A
        Book book1 = criarLivro("Dom Casmurro", "Machado de Assis", USER_A, StatusLeitura.LIDO, 1);
        Book book2 = criarLivro("O Alquimista", "Paulo Coelho", USER_A, StatusLeitura.LENDO, 2);
        Book book3 = criarLivro("1984", "George Orwell", USER_A, StatusLeitura.QUERO_LER, 3);
        bookRepository.saveAll(List.of(book1, book2, book3));

        // Livro do Usuário B — não deve aparecer nas consultas do A
        Book bookB = criarLivro("Sapiens", "Yuval Harari", USER_B, StatusLeitura.LIDO, 4);
        bookRepository.save(bookB);
    }

    @Test
    @DisplayName("RF-05: Deve listar apenas livros do usuário, ordenados por data desc")
    void deveListarLivrosDoUsuarioOrdenadosPorDataDesc() {
        List<Book> livros = bookRepository.findByUserIdOrderByDataCadastroDesc(USER_A);

        assertThat(livros).hasSize(3);
        assertThat(livros.get(0).getTitulo()).isEqualTo("1984"); // mais recente
        assertThat(livros.get(2).getTitulo()).isEqualTo("Dom Casmurro"); // mais antigo
    }

    @Test
    @DisplayName("RNF-05: Não deve retornar livros de outro usuário")
    void naoDeveRetornarLivrosDeOutroUsuario() {
        List<Book> livrosA = bookRepository.findByUserIdOrderByDataCadastroDesc(USER_A);
        List<Book> livrosB = bookRepository.findByUserIdOrderByDataCadastroDesc(USER_B);

        assertThat(livrosA).hasSize(3);
        assertThat(livrosB).hasSize(1);
        assertThat(livrosA).noneMatch(b -> b.getUserId().equals(USER_B));
    }

    @Test
    @DisplayName("RF-06: Deve buscar por título com regex case-insensitive")
    void deveBuscarPorTituloCaseInsensitive() {
        List<Book> resultado = bookRepository.searchByQuery("dom", USER_A);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Dom Casmurro");
    }

    @Test
    @DisplayName("RF-06: Deve buscar por autor com regex case-insensitive")
    void deveBuscarPorAutorCaseInsensitive() {
        List<Book> resultado = bookRepository.searchByQuery("machado", USER_A);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getAutor()).isEqualTo("Machado de Assis");
    }

    @Test
    @DisplayName("RF-06: Busca por query vazia retorna lista vazia do searchByQuery")
    void deveBuscarPorQueryVazia() {
        List<Book> resultado = bookRepository.searchByQuery("xyzinexistente", USER_A);
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("RF-06: Busca não deve encontrar livros de outro usuário")
    void buscaNaoDeveRetornarLivrosDeOutroUsuario() {
        List<Book> resultado = bookRepository.searchByQuery("sapiens", USER_A);
        assertThat(resultado).isEmpty();
    }

    private Book criarLivro(String titulo, String autor, String userId, StatusLeitura status, int diasAtras) {
        Book book = new Book(titulo, autor, null);
        book.setUserId(userId);
        book.setStatusLeitura(status);
        book.setDataCadastro(LocalDateTime.now().minusDays(diasAtras));
        book.setDataAtualizacao(LocalDateTime.now());
        return book;
    }
}
