package com.biblioteca.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes parametrizados de formato de CEP.
 * Conforme RF-11 seção 3.2.
 */
class CepFormatParamTest {

    private final BookValidator validator = new BookValidator();

    @ParameterizedTest(name = "[{index}] CEP ''{0}'' → válido={1}")
    @CsvSource({
        "01001000, true",
        "01001-000, true",
        "12345678, true",
        "12345-678, true",
        "00000001, true",
        "99999999, true",
        "00000000, false",
        "00000-000, false",
        "1234567, false",
        "123456789, false",
        "abcdefgh, false",
        "1234abcd, false",
        "'', false"
    })
    @DisplayName("RF-11: Validação de formato de CEP")
    void deveValidarFormatoCep(String cep, boolean esperadoValido) {
        assertThat(validator.isValidCep(cep)).isEqualTo(esperadoValido);
    }

    @ParameterizedTest(name = "[{index}] CEP null → válido=false")
    @CsvSource({ "false" })
    @DisplayName("RF-11: CEP null deve ser inválido")
    void cepNullDeveSerInvalido(boolean esperado) {
        assertThat(validator.isValidCep(null)).isEqualTo(esperado);
    }
}
