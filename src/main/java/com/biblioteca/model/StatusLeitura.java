package com.biblioteca.model;

/**
 * Enum que representa os possíveis status de leitura de um livro.
 * Conforme RF-04 seção 10.
 */
public enum StatusLeitura {

    QUERO_LER("Quero Ler"),
    LENDO("Lendo"),
    LIDO("Lido");

    private final String descricao;

    StatusLeitura(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
