package com.biblioteca.service;

import com.biblioteca.dto.BookDTO;
import com.biblioteca.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários do BookValidator.
 * Conforme RF-04 RN-01 a RN-04, RF-11.
 */
class BookValidatorTest {

    private BookValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BookValidator();
    }

    @Test
    @DisplayName("Deve aceitar livro com dados válidos completos")
    void deveAceitarDadosValidos() {
        BookDTO dto = new BookDTO("Dom Casmurro", "Machado de Assis", "978-85-7232-123-4");
        dto.setGenero("Romance");
        dto.setAnoPublicacao(1899);
        assertThatCode(() -> validator.validar(dto)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("RF-04 RN-01: Deve rejeitar título vazio")
    void deveRejeitarTituloVazio() {
        BookDTO dto = new BookDTO("", "Autor Válido", null);
        assertThatThrownBy(() -> validator.validar(dto))
            .isInstanceOf(ValidationException.class)
            .satisfies(ex -> assertThat(((ValidationException) ex).getErros())
                .contains("Título é obrigatório"));
    }

    @Test
    @DisplayName("RF-04 RN-01: Deve rejeitar título maior que 255 caracteres")
    void deveRejeitarTituloLongo() {
        BookDTO dto = new BookDTO("A".repeat(256), "Autor", null);
        assertThatThrownBy(() -> validator.validar(dto))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("RF-04 RN-02: Deve rejeitar autor com menos de 2 caracteres")
    void deveRejeitarAutorCurto() {
        BookDTO dto = new BookDTO("Título", "A", null);
        assertThatThrownBy(() -> validator.validar(dto))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("RF-04 RN-03: Deve aceitar ISBN-10 válido")
    void deveAceitarIsbn10Valido() {
        assertThat(validator.isValidIsbn("0306406152")).isTrue();
        assertThat(validator.isValidIsbn("030640615X")).isTrue();
    }

    @Test
    @DisplayName("RF-04 RN-03: Deve aceitar ISBN-13 válido")
    void deveAceitarIsbn13Valido() {
        assertThat(validator.isValidIsbn("9780306406157")).isTrue();
    }

    @Test
    @DisplayName("RF-04 RN-03: Deve rejeitar ISBN com formato inválido")
    void deveRejeitarIsbnInvalido() {
        assertThat(validator.isValidIsbn("123")).isFalse();
        assertThat(validator.isValidIsbn("abcdefghij")).isFalse();
    }

    @Test
    @DisplayName("RF-04 RN-04: Deve rejeitar ano < 1450")
    void deveRejeitarAnoAnteriorA1450() {
        BookDTO dto = new BookDTO("Título", "Autor Válido", null);
        dto.setAnoPublicacao(1449);
        assertThatThrownBy(() -> validator.validar(dto))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("RF-04 RN-04: Deve rejeitar ano futuro")
    void deveRejeitarAnoFuturo() {
        BookDTO dto = new BookDTO("Título", "Autor Válido", null);
        dto.setAnoPublicacao(3000);
        assertThatThrownBy(() -> validator.validar(dto))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Deve aceitar campos opcionais como null")
    void deveAceitarCamposOpcionaisNull() {
        BookDTO dto = new BookDTO("Título", "Autor Válido", null);
        assertThatCode(() -> validator.validar(dto)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("RF-11: CEP válido com 8 dígitos")
    void cepValido() {
        assertThat(validator.isValidCep("01001000")).isTrue();
        assertThat(validator.isValidCep("01001-000")).isTrue();
    }

    @Test
    @DisplayName("RF-11: CEP inválido — todos zeros")
    void cepInvalidoTodosZeros() {
        assertThat(validator.isValidCep("00000000")).isFalse();
    }

    @Test
    @DisplayName("RF-11: CEP inválido — menos de 8 dígitos")
    void cepInvalidoPoucoDigitos() {
        assertThat(validator.isValidCep("1234")).isFalse();
    }

    @Test
    @DisplayName("RF-11: CEP inválido — null ou vazio")
    void cepInvalidoNullOuVazio() {
        assertThat(validator.isValidCep(null)).isFalse();
        assertThat(validator.isValidCep("")).isFalse();
    }

    @Test
    @DisplayName("RF-11 CA-08: Deve acumular múltiplos erros")
    void deveAcumularMultiplosErros() {
        BookDTO dto = new BookDTO();
        // titulo null, autor null -> 2 erros
        assertThatThrownBy(() -> validator.validar(dto))
            .isInstanceOf(ValidationException.class)
            .satisfies(ex -> assertThat(((ValidationException) ex).getErros()).hasSizeGreaterThanOrEqualTo(2));
    }
}
