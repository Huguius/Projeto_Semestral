package com.biblioteca.exception;

/**
 * Exceção lançada quando um email já existe no sistema.
 * Conforme RF-01 RN-04.
 */
public class EmailDuplicadoException extends RuntimeException {
    public EmailDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
