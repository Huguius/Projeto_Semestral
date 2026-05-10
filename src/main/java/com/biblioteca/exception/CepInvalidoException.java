package com.biblioteca.exception;

/**
 * Exceção lançada quando o formato do CEP é inválido.
 * Conforme RF-11 CA-03.
 */
public class CepInvalidoException extends RuntimeException {
    public CepInvalidoException(String mensagem) {
        super(mensagem);
    }
}
