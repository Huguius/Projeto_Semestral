package com.biblioteca.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes do GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Deve retornar error/404 para LivroNaoEncontradoException")
    void deveRetornar404ParaLivroNaoEncontrado() {
        Model model = new ExtendedModelMap();
        LivroNaoEncontradoException ex = new LivroNaoEncontradoException("Não encontrado");

        String view = handler.handleNotFound(ex, model);

        assertThat(view).isEqualTo("error/404");
        assertThat(model.getAttribute("erro")).isEqualTo("Não encontrado");
    }

    @Test
    @DisplayName("Deve retornar error/403 para AccessDeniedException")
    void deveRetornar403ParaAccessDenied() {
        Model model = new ExtendedModelMap();
        AccessDeniedException ex = new AccessDeniedException("Proibido");

        String view = handler.handleAccessDenied(ex, model);

        assertThat(view).isEqualTo("error/403");
        assertThat(model.getAttribute("erro")).isEqualTo("Você não tem permissão para acessar este recurso.");
    }
}
