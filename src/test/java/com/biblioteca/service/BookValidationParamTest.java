package com.biblioteca.service;

import com.biblioteca.dto.BookDTO;
import com.biblioteca.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Testes parametrizados de validação de livro.
 * Conforme RNF-01 seção 5.4 — cobertura de fronteira.
 */
class BookValidationParamTest {

    private final BookValidator validator = new BookValidator();

    @ParameterizedTest(name = "[{index}] titulo=''{0}'', autor=''{1}'', isbn=''{2}'', ano={3} → válido={4}")
    @MethodSource("dadosValidacao")
    @DisplayName("RF-11: Validação parametrizada de livro")
    void deveValidarLivro(String titulo, String autor, String isbn, Integer ano, boolean esperadoValido) {
        BookDTO dto = new BookDTO(titulo, autor, isbn);
        dto.setAnoPublicacao(ano);

        if (esperadoValido) {
            assertThatCode(() -> validator.validar(dto)).doesNotThrowAnyException();
        } else {
            assertThatThrownBy(() -> validator.validar(dto))
                .isInstanceOf(ValidationException.class);
        }
    }

    static Stream<Arguments> dadosValidacao() {
        return Stream.of(
            // titulo, autor, isbn, ano, válido?
            arguments("Dom Casmurro", "Machado de Assis", null, null, true),
            arguments("Dom Casmurro", "Machado de Assis", "9780306406157", 1899, true),
            arguments("Dom Casmurro", "Machado de Assis", "030640615X", 2020, true),
            arguments(null, "Autor", null, null, false),           // titulo null
            arguments("", "Autor", null, null, false),             // titulo vazio
            arguments("   ", "Autor", null, null, false),          // titulo só espaços
            arguments("T", null, null, null, false),               // autor null
            arguments("T", "", null, null, false),                 // autor vazio
            arguments("T", "A", null, null, false),                // autor < 2 chars
            arguments("Título", "Autor OK", "123", null, false),   // ISBN inválido
            arguments("Título", "Autor OK", null, 1449, false),    // ano < 1450
            arguments("Título", "Autor OK", null, 3000, false),    // ano futuro
            arguments("Título", "Autor OK", null, 1450, true),     // ano fronteira baixa
            arguments("Título", "Autor OK", "", null, true)        // ISBN vazio = opcional
        );
    }
}
