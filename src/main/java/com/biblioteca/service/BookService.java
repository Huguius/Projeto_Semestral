package com.biblioteca.service;

import com.biblioteca.dto.BookDTO;
import com.biblioteca.exception.LivroNaoEncontradoException;
import com.biblioteca.model.Book;
import com.biblioteca.model.StatusLeitura;
import com.biblioteca.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço de livros — CRUD com verificação de propriedade por userId.
 * Controller NUNCA acessa Repository diretamente (RNF-08 CV-02).
 */
@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final BookValidator bookValidator;

    public BookService(BookRepository bookRepository, BookValidator bookValidator) {
        this.bookRepository = bookRepository;
        this.bookValidator = bookValidator;
    }

    /**
     * Cria um novo livro associado ao usuário.
     * Status padrão QUERO_LER se não informado (RF-04 RN-05).
     */
    public Book criarLivro(BookDTO dto, String userId) {
        bookValidator.validar(dto);

        Book book = new Book();
        book.setUserId(userId);
        book.setTitulo(dto.getTitulo());
        book.setAutor(dto.getAutor());
        book.setIsbn(dto.getIsbn());
        book.setGenero(dto.getGenero());
        book.setAnoPublicacao(dto.getAnoPublicacao());
        book.setStatusLeitura(dto.getStatusLeitura() != null ? dto.getStatusLeitura() : StatusLeitura.QUERO_LER);
        book.setDataCadastro(LocalDateTime.now());
        book.setDataAtualizacao(LocalDateTime.now());

        log.info("Criando livro '{}' para usuário {}", dto.getTitulo(), userId);
        return bookRepository.save(book);
    }

    /**
     * Lista livros do usuário, ordenados por data de cadastro decrescente.
     * Conforme RF-05. NUNCA usa findAll() sem filtro userId (RNF-05 seção 3.4).
     */
    public List<Book> listarLivros(String userId) {
        return bookRepository.findByUserIdOrderByDataCadastroDesc(userId);
    }

    /**
     * Busca livros por query (título, autor ou gênero).
     * Query vazia retorna todos os livros do usuário (RF-06 CA-07).
     */
    public List<Book> buscarLivros(String query, String userId) {
        if (query == null || query.trim().isEmpty()) {
            return listarLivros(userId);
        }
        return bookRepository.searchByQuery(query.trim(), userId);
    }

    /**
     * Busca livro por ID com verificação de propriedade.
     * Lança AccessDeniedException se o livro pertence a outro usuário (RF-07/08/09).
     */
    public Book buscarPorId(String id, String userId) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new LivroNaoEncontradoException("Livro não encontrado"));

        if (!book.getUserId().equals(userId)) {
            throw new AccessDeniedException("Acesso negado");
        }

        return book;
    }

    /**
     * Atualiza livro existente com verificação de propriedade.
     * Atualiza dataAtualizacao (RF-07 RN-02). dataCadastro permanece inalterada.
     */
    public Book atualizarLivro(String id, BookDTO dto, String userId) {
        Book book = buscarPorId(id, userId);
        bookValidator.validar(dto);

        book.setTitulo(dto.getTitulo());
        book.setAutor(dto.getAutor());
        book.setIsbn(dto.getIsbn());
        book.setGenero(dto.getGenero());
        book.setAnoPublicacao(dto.getAnoPublicacao());
        book.setStatusLeitura(dto.getStatusLeitura() != null ? dto.getStatusLeitura() : book.getStatusLeitura());
        book.setDataAtualizacao(LocalDateTime.now());

        log.info("Atualizando livro '{}' (id: {})", dto.getTitulo(), id);
        return bookRepository.save(book);
    }

    /**
     * Exclui livro com verificação de propriedade.
     * Hard delete conforme RF-08 RN-02.
     */
    public void excluirLivro(String id, String userId) {
        Book book = buscarPorId(id, userId);
        log.info("Excluindo livro '{}' (id: {})", book.getTitulo(), id);
        bookRepository.deleteById(id);
    }
}
