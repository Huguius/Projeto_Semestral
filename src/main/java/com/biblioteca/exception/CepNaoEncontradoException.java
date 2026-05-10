package com.biblioteca.exception;

/**
 * Exceção lançada quando o CEP informado não é encontrado na API ViaCEP.
 * Conforme RF-10 CA-04.
 */
public class CepNaoEncontradoException extends RuntimeException {
    public CepNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
