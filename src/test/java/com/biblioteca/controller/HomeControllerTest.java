package com.biblioteca.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes do HomeController.
 */
class HomeControllerTest {

    private final HomeController controller = new HomeController();

    @Test
    @DisplayName("Deve redirecionar para /books quando autenticado")
    void deveRedirecionarParaBooksQuandoAutenticado() {
        // Precisa passar authorities para isAuthenticated() retornar true
        Authentication auth = new UsernamePasswordAuthenticationToken(
            "user", "pass", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        String result = controller.home(auth);
        assertThat(result).isEqualTo("redirect:/books");
    }

    @Test
    @DisplayName("Deve redirecionar para /login quando não autenticado")
    void deveRedirecionarParaLoginQuandoNaoAutenticado() {
        String result = controller.home(null);
        assertThat(result).isEqualTo("redirect:/login");
    }
}
