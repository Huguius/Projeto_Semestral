package com.biblioteca.dto;

import com.biblioteca.model.StatusLeitura;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes dos DTOs — cobertura de getters/setters.
 */
class DtoTest {

    @Test
    @DisplayName("UserDTO: todos os getters e setters")
    void userDtoGettersSetters() {
        UserDTO dto = new UserDTO();
        dto.setNome("João");
        dto.setEmail("joao@email.com");
        dto.setSenha("12345678");
        dto.setConfirmacaoSenha("12345678");
        dto.setCep("01001000");
        dto.setLogradouro("Rua");
        dto.setBairro("Bairro");
        dto.setCidade("Cidade");
        dto.setUf("SP");
        dto.setNumero("100");
        dto.setComplemento("Apto 1");

        assertThat(dto.getNome()).isEqualTo("João");
        assertThat(dto.getEmail()).isEqualTo("joao@email.com");
        assertThat(dto.getSenha()).isEqualTo("12345678");
        assertThat(dto.getConfirmacaoSenha()).isEqualTo("12345678");
        assertThat(dto.getCep()).isEqualTo("01001000");
        assertThat(dto.getLogradouro()).isEqualTo("Rua");
        assertThat(dto.getBairro()).isEqualTo("Bairro");
        assertThat(dto.getCidade()).isEqualTo("Cidade");
        assertThat(dto.getUf()).isEqualTo("SP");
        assertThat(dto.getNumero()).isEqualTo("100");
        assertThat(dto.getComplemento()).isEqualTo("Apto 1");
    }

    @Test
    @DisplayName("BookDTO: todos os getters e setters")
    void bookDtoGettersSetters() {
        BookDTO dto = new BookDTO();
        dto.setTitulo("Título");
        dto.setAutor("Autor");
        dto.setIsbn("1234567890");
        dto.setGenero("Romance");
        dto.setAnoPublicacao(2020);
        dto.setStatusLeitura(StatusLeitura.LIDO);

        assertThat(dto.getTitulo()).isEqualTo("Título");
        assertThat(dto.getAutor()).isEqualTo("Autor");
        assertThat(dto.getIsbn()).isEqualTo("1234567890");
        assertThat(dto.getGenero()).isEqualTo("Romance");
        assertThat(dto.getAnoPublicacao()).isEqualTo(2020);
        assertThat(dto.getStatusLeitura()).isEqualTo(StatusLeitura.LIDO);
    }

    @Test
    @DisplayName("BookDTO: construtor com argumentos")
    void bookDtoConstrutorComArgs() {
        BookDTO dto = new BookDTO("T", "A", "ISBN");
        assertThat(dto.getTitulo()).isEqualTo("T");
        assertThat(dto.getAutor()).isEqualTo("A");
        assertThat(dto.getIsbn()).isEqualTo("ISBN");
    }

    @Test
    @DisplayName("EnderecoInfo: todos os getters e setters")
    void enderecoInfoGettersSetters() {
        EnderecoInfo info = new EnderecoInfo();
        info.setLogradouro("Rua A");
        info.setBairro("Bairro B");
        info.setLocalidade("Cidade C");
        info.setUf("SP");

        assertThat(info.getLogradouro()).isEqualTo("Rua A");
        assertThat(info.getBairro()).isEqualTo("Bairro B");
        assertThat(info.getLocalidade()).isEqualTo("Cidade C");
        assertThat(info.getUf()).isEqualTo("SP");
    }

    @Test
    @DisplayName("EnderecoInfo: construtor com argumentos")
    void enderecoInfoConstrutorComArgs() {
        EnderecoInfo info = new EnderecoInfo("R", "B", "C", "UF");
        assertThat(info.getLogradouro()).isEqualTo("R");
        assertThat(info.getBairro()).isEqualTo("B");
        assertThat(info.getLocalidade()).isEqualTo("C");
        assertThat(info.getUf()).isEqualTo("UF");
    }
}
