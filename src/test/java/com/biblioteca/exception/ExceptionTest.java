package com.biblioteca.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes das exceções customizadas.
 */
class ExceptionTest {

    @Test
    @DisplayName("ValidationException: deve carregar lista de erros")
    void validationException() {
        List<String> erros = List.of("Erro 1", "Erro 2");
        ValidationException ex = new ValidationException(erros);
        assertThat(ex.getErros()).hasSize(2);
        assertThat(ex.getMessage()).contains("Erro 1", "Erro 2");
    }

    @Test
    @DisplayName("EmailDuplicadoException: deve manter mensagem")
    void emailDuplicadoException() {
        EmailDuplicadoException ex = new EmailDuplicadoException("Email já existe");
        assertThat(ex.getMessage()).isEqualTo("Email já existe");
    }

    @Test
    @DisplayName("CepInvalidoException: deve manter mensagem")
    void cepInvalidoException() {
        CepInvalidoException ex = new CepInvalidoException("CEP inválido");
        assertThat(ex.getMessage()).isEqualTo("CEP inválido");
    }

    @Test
    @DisplayName("CepNaoEncontradoException: deve manter mensagem")
    void cepNaoEncontradoException() {
        CepNaoEncontradoException ex = new CepNaoEncontradoException("Não encontrado");
        assertThat(ex.getMessage()).isEqualTo("Não encontrado");
    }

    @Test
    @DisplayName("ApiIndisponivelException: deve manter mensagem")
    void apiIndisponivelException() {
        ApiIndisponivelException ex = new ApiIndisponivelException("Indisponível");
        assertThat(ex.getMessage()).isEqualTo("Indisponível");
    }

    @Test
    @DisplayName("LivroNaoEncontradoException: deve manter mensagem")
    void livroNaoEncontradoException() {
        LivroNaoEncontradoException ex = new LivroNaoEncontradoException("Não achei");
        assertThat(ex.getMessage()).isEqualTo("Não achei");
    }
}
