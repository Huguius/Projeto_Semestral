package com.biblioteca.controller;

import com.biblioteca.dto.BookDTO;
import com.biblioteca.exception.ValidationException;
import com.biblioteca.model.Book;
import com.biblioteca.model.StatusLeitura;
import com.biblioteca.model.User;
import com.biblioteca.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller de livros — CRUD completo.
 * Obtém userId via @AuthenticationPrincipal.
 * Conforme RF-04 a RF-09.
 */
@Controller
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Lista livros do usuário ou busca por query.
     * GET /books ou GET /books?q=... (RF-05 + RF-06).
     */
    @GetMapping("/books")
    public String listarOuBuscar(@RequestParam(value = "q", required = false) String query,
                                  @AuthenticationPrincipal User user,
                                  Model model) {
        List<Book> livros;
        if (query != null && !query.trim().isEmpty()) {
            livros = bookService.buscarLivros(query, user.getId());
            model.addAttribute("query", query);
        } else {
            livros = bookService.listarLivros(user.getId());
        }
        model.addAttribute("books", livros);
        return "books/list";
    }

    /**
     * Exibe formulário de criação de livro.
     * GET /books/new (RF-04).
     */
    @GetMapping("/books/new")
    public String novoLivroForm(Model model) {
        model.addAttribute("book", new BookDTO());
        model.addAttribute("statusOptions", StatusLeitura.values());
        return "books/form";
    }

    /**
     * Processa criação de livro.
     * POST /books (RF-04 CA-05).
     */
    @PostMapping("/books")
    public String criarLivro(@ModelAttribute BookDTO bookDTO,
                              @AuthenticationPrincipal User user,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            bookService.criarLivro(bookDTO, user.getId());
            redirectAttributes.addFlashAttribute("mensagem", "Livro cadastrado com sucesso!");
            return "redirect:/books";
        } catch (ValidationException e) {
            model.addAttribute("book", bookDTO);
            model.addAttribute("erros", e.getErros());
            model.addAttribute("statusOptions", StatusLeitura.values());
            return "books/form";
        }
    }

    /**
     * Exibe detalhes de um livro.
     * GET /books/{id} (RF-09).
     */
    @GetMapping("/books/{id}")
    public String detalhesLivro(@PathVariable String id,
                                 @AuthenticationPrincipal User user,
                                 Model model) {
        Book book = bookService.buscarPorId(id, user.getId());
        model.addAttribute("book", book);
        return "books/detail";
    }

    /**
     * Exibe formulário de edição pré-preenchido.
     * GET /books/{id}/edit (RF-07 CA-01).
     */
    @GetMapping("/books/{id}/edit")
    public String editarLivroForm(@PathVariable String id,
                                   @AuthenticationPrincipal User user,
                                   Model model) {
        Book book = bookService.buscarPorId(id, user.getId());

        BookDTO dto = new BookDTO();
        dto.setTitulo(book.getTitulo());
        dto.setAutor(book.getAutor());
        dto.setIsbn(book.getIsbn());
        dto.setGenero(book.getGenero());
        dto.setAnoPublicacao(book.getAnoPublicacao());
        dto.setStatusLeitura(book.getStatusLeitura());

        model.addAttribute("book", dto);
        model.addAttribute("bookId", book.getId());
        model.addAttribute("statusOptions", StatusLeitura.values());
        return "books/form";
    }

    /**
     * Processa edição de livro.
     * POST /books/{id}/edit (RF-07 CA-04).
     */
    @PostMapping("/books/{id}/edit")
    public String atualizarLivro(@PathVariable String id,
                                  @ModelAttribute BookDTO bookDTO,
                                  @AuthenticationPrincipal User user,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            bookService.atualizarLivro(id, bookDTO, user.getId());
            redirectAttributes.addFlashAttribute("mensagem", "Livro atualizado com sucesso!");
            return "redirect:/books";
        } catch (ValidationException e) {
            model.addAttribute("book", bookDTO);
            model.addAttribute("bookId", id);
            model.addAttribute("erros", e.getErros());
            model.addAttribute("statusOptions", StatusLeitura.values());
            return "books/form";
        }
    }

    /**
     * Processa exclusão de livro.
     * POST /books/{id}/delete (RF-08 CA-06). Sempre POST com CSRF.
     */
    @PostMapping("/books/{id}/delete")
    public String excluirLivro(@PathVariable String id,
                                @AuthenticationPrincipal User user,
                                RedirectAttributes redirectAttributes) {
        bookService.excluirLivro(id, user.getId());
        redirectAttributes.addFlashAttribute("mensagem", "Livro excluído com sucesso!");
        return "redirect:/books";
    }
}
