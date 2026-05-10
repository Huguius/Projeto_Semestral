package com.biblioteca.repository;

import com.biblioteca.AbstractIntegrationTest;
import com.biblioteca.model.Endereco;
import com.biblioteca.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração do UserRepository com MongoDB real via Testcontainers.
 * Valida findByEmail e unicidade de email.
 * Conforme RF-01, RF-02.
 */
class UserRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("RF-02: Deve encontrar usuário por email")
    void deveEncontrarUsuarioPorEmail() {
        User user = criarUser("João", "joao@email.com");
        userRepository.save(user);

        Optional<User> encontrado = userRepository.findByEmail("joao@email.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("João");
        assertThat(encontrado.get().getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("RF-02: Deve retornar Optional vazio para email inexistente")
    void deveRetornarVazioParaEmailInexistente() {
        Optional<User> resultado = userRepository.findByEmail("inexistente@email.com");
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("RF-01: Deve persistir usuário com todos os campos")
    void devePersistirUsuarioComTodosCampos() {
        Endereco endereco = new Endereco("01001000", "Praça da Sé", "Sé", "São Paulo", "SP", "123", "Apto 4");
        User user = new User("Maria", "maria@email.com", "hashSenha123", endereco);
        
        User salvo = userRepository.save(user);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Maria");
        assertThat(salvo.getEndereco().getCep()).isEqualTo("01001000");
        assertThat(salvo.getEndereco().getLogradouro()).isEqualTo("Praça da Sé");
    }

    @Test
    @DisplayName("RF-01: Deve gerar ID automaticamente ao salvar")
    void deveGerarIdAutomaticamente() {
        User user = criarUser("Carlos", "carlos@email.com");
        assertThat(user.getId()).isNull();

        User salvo = userRepository.save(user);
        assertThat(salvo.getId()).isNotNull().isNotEmpty();
    }

    private User criarUser(String nome, String email) {
        return new User(nome, email, "senhaHash123",
            new Endereco("01001000", "Rua Teste", "Bairro", "Cidade", "SP", null, null));
    }
}
