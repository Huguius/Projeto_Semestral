package com.biblioteca.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários das entidades User e Book.
 * Cobrem getters/setters, UserDetails, e construtores.
 */
class ModelTest {

    @Test
    @DisplayName("User: Deve implementar UserDetails corretamente")
    void userDeveImplementarUserDetails() {
        Endereco endereco = new Endereco("01001000", "Rua", "Bairro", "Cidade", "SP", "123", "Apto 4");
        User user = new User("João", "joao@email.com", "$2a$hash", endereco);
        user.setId("user-1");
        user.setDataCriacao(LocalDateTime.now());

        assertThat(user.getUsername()).isEqualTo("joao@email.com");
        assertThat(user.getPassword()).isEqualTo("$2a$hash");
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
        assertThat(user.getId()).isEqualTo("user-1");
        assertThat(user.getNome()).isEqualTo("João");
        assertThat(user.getEmail()).isEqualTo("joao@email.com");
        assertThat(user.getSenhaHash()).isEqualTo("$2a$hash");
        assertThat(user.getEndereco()).isNotNull();
        assertThat(user.getDataCriacao()).isNotNull();
    }

    @Test
    @DisplayName("User: Construtor vazio deve funcionar")
    void userConstrutorVazio() {
        User user = new User();
        user.setNome("Maria");
        user.setEmail("maria@email.com");
        user.setSenhaHash("hash");
        user.setEndereco(null);
        user.setId("id-2");
        user.setDataCriacao(LocalDateTime.now());

        assertThat(user.getNome()).isEqualTo("Maria");
        assertThat(user.getEmail()).isEqualTo("maria@email.com");
    }

    @Test
    @DisplayName("Book: Deve construir com 3 argumentos")
    void bookConstrutorComArgs() {
        Book book = new Book("Título", "Autor", "1234567890");
        assertThat(book.getTitulo()).isEqualTo("Título");
        assertThat(book.getAutor()).isEqualTo("Autor");
        assertThat(book.getIsbn()).isEqualTo("1234567890");
        assertThat(book.getDataCadastro()).isNotNull();
        assertThat(book.getDataAtualizacao()).isNotNull();
    }

    @Test
    @DisplayName("Book: Construtor vazio e setters")
    void bookConstrutorVazioESetters() {
        Book book = new Book();
        book.setId("b-1");
        book.setUserId("u-1");
        book.setTitulo("Teste");
        book.setAutor("Autor");
        book.setIsbn("isbn");
        book.setGenero("Ficção");
        book.setAnoPublicacao(2020);
        book.setStatusLeitura(StatusLeitura.LENDO);
        book.setDataCadastro(LocalDateTime.now());
        book.setDataAtualizacao(LocalDateTime.now());

        assertThat(book.getId()).isEqualTo("b-1");
        assertThat(book.getUserId()).isEqualTo("u-1");
        assertThat(book.getTitulo()).isEqualTo("Teste");
        assertThat(book.getAutor()).isEqualTo("Autor");
        assertThat(book.getIsbn()).isEqualTo("isbn");
        assertThat(book.getGenero()).isEqualTo("Ficção");
        assertThat(book.getAnoPublicacao()).isEqualTo(2020);
        assertThat(book.getStatusLeitura()).isEqualTo(StatusLeitura.LENDO);
    }

    @Test
    @DisplayName("Endereco: Todos os getters e setters")
    void enderecoGettersSetters() {
        Endereco e = new Endereco();
        e.setCep("01001000");
        e.setLogradouro("Rua");
        e.setBairro("Bairro");
        e.setCidade("Cidade");
        e.setUf("SP");
        e.setNumero("100");
        e.setComplemento("Apto");

        assertThat(e.getCep()).isEqualTo("01001000");
        assertThat(e.getLogradouro()).isEqualTo("Rua");
        assertThat(e.getBairro()).isEqualTo("Bairro");
        assertThat(e.getCidade()).isEqualTo("Cidade");
        assertThat(e.getUf()).isEqualTo("SP");
        assertThat(e.getNumero()).isEqualTo("100");
        assertThat(e.getComplemento()).isEqualTo("Apto");
    }

    @Test
    @DisplayName("StatusLeitura: Deve retornar descrição correta")
    void statusLeituraDescricao() {
        assertThat(StatusLeitura.QUERO_LER.getDescricao()).isEqualTo("Quero Ler");
        assertThat(StatusLeitura.LENDO.getDescricao()).isEqualTo("Lendo");
        assertThat(StatusLeitura.LIDO.getDescricao()).isEqualTo("Lido");
        assertThat(StatusLeitura.values()).hasSize(3);
    }
}
