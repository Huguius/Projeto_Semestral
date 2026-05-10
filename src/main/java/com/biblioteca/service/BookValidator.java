package com.biblioteca.service;

import com.biblioteca.dto.BookDTO;
import com.biblioteca.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * Validador de dados de livro.
 * Classe separada conforme SRP (RNF-08 seção 4).
 * Coleta TODOS os erros antes de lançar exceção (RF-11 CA-08).
 */
@Component
public class BookValidator {

    /**
     * Valida os dados do livro conforme RF-11 seção 3.1.
     * Lança ValidationException com lista de erros se houver problemas.
     */
    public void validar(BookDTO dto) {
        List<String> erros = new ArrayList<>();

        // Título: obrigatório, 1-255 caracteres (RF-04 RN-01)
        if (isBlank(dto.getTitulo())) {
            erros.add("Título é obrigatório");
        } else if (dto.getTitulo().length() > 255) {
            erros.add("Título muito longo (máximo 255 caracteres)");
        }

        // Autor: obrigatório, 2-255 caracteres (RF-04 RN-02)
        if (isBlank(dto.getAutor()) || dto.getAutor().trim().length() < 2) {
            erros.add("Autor é obrigatório (mínimo 2 caracteres)");
        } else if (dto.getAutor().length() > 255) {
            erros.add("Autor muito longo (máximo 255 caracteres)");
        }

        // ISBN: opcional; se informado, validar formato (RF-04 RN-03)
        if (dto.getIsbn() != null && !dto.getIsbn().isEmpty()) {
            if (!isValidIsbn(dto.getIsbn())) {
                erros.add("Formato de ISBN inválido");
            }
        }

        // Ano de publicação: opcional; se informado, entre 1450 e ano atual (RF-04 RN-04)
        if (dto.getAnoPublicacao() != null) {
            int anoAtual = Year.now().getValue();
            if (dto.getAnoPublicacao() < 1450 || dto.getAnoPublicacao() > anoAtual) {
                erros.add("Ano de publicação inválido");
            }
        }

        // Gênero: opcional, máximo 100 caracteres
        if (dto.getGenero() != null && dto.getGenero().length() > 100) {
            erros.add("Gênero muito longo (máximo 100 caracteres)");
        }

        if (!erros.isEmpty()) {
            throw new ValidationException(erros);
        }
    }

    /**
     * Valida formato de CEP: 8 dígitos numéricos, não todos zeros.
     * Conforme RF-11 CA-03, RF-11 seção 3.2.
     */
    public boolean isValidCep(String cep) {
        if (cep == null || cep.isEmpty()) {
            return false;
        }
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            return false;
        }
        return !cepLimpo.matches("^0+$");
    }

    /**
     * Valida formato ISBN-10 ou ISBN-13.
     * ISBN-10: 10 dígitos (último pode ser X).
     * ISBN-13: 13 dígitos.
     */
    public boolean isValidIsbn(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            return false;
        }
        String isbnLimpo = isbn.replaceAll("[\\s-]", "");

        if (isbnLimpo.length() == 10) {
            return isbnLimpo.matches("^\\d{9}[\\dXx]$");
        } else if (isbnLimpo.length() == 13) {
            return isbnLimpo.matches("^\\d{13}$");
        }
        return false;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
