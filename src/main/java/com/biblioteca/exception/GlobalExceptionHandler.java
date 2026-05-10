package com.biblioteca.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Handler global de exceções.
 * Centraliza o tratamento de erros conforme RNF-08 seção 5.3.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata livro não encontrado → página 404.
     */
    @ExceptionHandler(LivroNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(LivroNaoEncontradoException ex, Model model) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        model.addAttribute("erro", ex.getMessage());
        return "error/404";
    }

    /**
     * Trata acesso negado → página 403.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        log.warn("Acesso negado: {}", ex.getMessage());
        model.addAttribute("erro", "Você não tem permissão para acessar este recurso.");
        return "error/403";
    }
}
