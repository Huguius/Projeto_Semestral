package com.biblioteca.service;

import com.biblioteca.dto.UserDTO;
import com.biblioteca.exception.EmailDuplicadoException;
import com.biblioteca.exception.ValidationException;
import com.biblioteca.model.User;
import com.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;
    private UserDTO dtoValido;

    @BeforeEach
    void setUp() {
        dtoValido = new UserDTO();
        dtoValido.setNome("João Silva");
        dtoValido.setEmail("joao@email.com");
        dtoValido.setSenha("12345678");
        dtoValido.setConfirmacaoSenha("12345678");
        dtoValido.setCep("01001000");
    }

    @Test
    @DisplayName("RF-01: Deve registrar usuário com dados válidos")
    void deveRegistrarUsuarioValido() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("12345678")).thenReturn("$2a$10$hash");
        when(userRepository.save(any(User.class))).thenAnswer(i -> { User u = i.getArgument(0); u.setId("id1"); return u; });

        User r = userService.registrar(dtoValido);
        assertThat(r.getId()).isEqualTo("id1");
        assertThat(r.getNome()).isEqualTo("João Silva");
        assertThat(r.getSenhaHash()).isEqualTo("$2a$10$hash");
    }

    @Test
    @DisplayName("RF-01 CA-04: Deve hashear senha com BCrypt")
    void deveHashearSenha() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("12345678")).thenReturn("$2a$10$hash");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.registrar(dtoValido);
        ArgumentCaptor<User> c = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(c.capture());
        assertThat(c.getValue().getSenhaHash()).startsWith("$2a$10$");
    }

    @Test
    @DisplayName("RF-01 RN-03: Deve rejeitar nome vazio")
    void deveRejeitarNomeVazio() {
        dtoValido.setNome("");
        assertThatThrownBy(() -> userService.registrar(dtoValido))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("RF-01 RN-01: Deve rejeitar email inválido")
    void deveRejeitarEmailInvalido() {
        dtoValido.setEmail("semArroba");
        assertThatThrownBy(() -> userService.registrar(dtoValido))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("RF-01 CA-03: Deve rejeitar senha < 8 caracteres")
    void deveRejeitarSenhaCurta() {
        dtoValido.setSenha("1234567"); dtoValido.setConfirmacaoSenha("1234567");
        assertThatThrownBy(() -> userService.registrar(dtoValido))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("RF-01 RN-02: Deve rejeitar senhas diferentes")
    void deveRejeitarSenhasDiferentes() {
        dtoValido.setConfirmacaoSenha("87654321");
        assertThatThrownBy(() -> userService.registrar(dtoValido))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("RF-01 RN-04: Deve rejeitar email duplicado")
    void deveRejeitarEmailDuplicado() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(new User()));
        assertThatThrownBy(() -> userService.registrar(dtoValido))
            .isInstanceOf(EmailDuplicadoException.class);
    }

    @Test
    @DisplayName("RF-11: Deve acumular múltiplos erros")
    void deveAcumularErros() {
        dtoValido.setNome(""); dtoValido.setEmail("x"); dtoValido.setSenha("1"); dtoValido.setConfirmacaoSenha("2");
        assertThatThrownBy(() -> userService.registrar(dtoValido))
            .isInstanceOf(ValidationException.class)
            .satisfies(ex -> assertThat(((ValidationException)ex).getErros()).hasSizeGreaterThanOrEqualTo(3));
    }

    @Test
    @DisplayName("RF-02: Deve carregar usuário por email")
    void deveCarregarPorEmail() {
        User u = new User("J", "joao@email.com", "h", null);
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(u));
        assertThat(userService.loadUserByUsername("joao@email.com").getUsername()).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("RF-02: Deve lançar exceção para email não encontrado")
    void deveLancarExcecaoEmailNaoEncontrado() {
        when(userRepository.findByEmail("x@x.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.loadUserByUsername("x@x.com"))
            .isInstanceOf(UsernameNotFoundException.class);
    }
}
