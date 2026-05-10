package com.biblioteca.exception;

/**
 * Exceção lançada quando um livro não é encontrado no banco.
 * Conforme RF-07 CA-07.
 */
public class LivroNaoEncontradoException extends RuntimeException {
    public LivroNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
