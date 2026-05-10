package com.biblioteca.exception;

/**
 * Exceção lançada quando a API externa (ViaCEP) está indisponível ou timeout.
 * Conforme RF-10 CA-07.
 */
public class ApiIndisponivelException extends RuntimeException {
    public ApiIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
