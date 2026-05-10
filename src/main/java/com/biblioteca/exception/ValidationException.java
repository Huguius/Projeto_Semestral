package com.biblioteca.exception;

import java.util.List;

/**
 * Exceção de validação que carrega uma lista de erros.
 * Permite exibir múltiplos erros simultaneamente (RF-11 CA-08).
 */
public class ValidationException extends RuntimeException {

    private final List<String> erros;

    public ValidationException(List<String> erros) {
        super("Erros de validação: " + String.join(", ", erros));
        this.erros = erros;
    }

    public List<String> getErros() {
        return erros;
    }
}
