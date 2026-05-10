package com.biblioteca.controller;

import com.biblioteca.dto.UserDTO;
import com.biblioteca.exception.EmailDuplicadoException;
import com.biblioteca.exception.ValidationException;
import com.biblioteca.model.User;
import com.biblioteca.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de POST /register do AuthController.
 * Conforme RF-01.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerRegisterTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setViewResolvers(viewResolver)
            .build();
    }

    @Test
    @DisplayName("RF-01: POST /register com dados válidos deve redirecionar para login")
    void deveRedirecionarParaLoginAposCadastro() throws Exception {
        User user = new User("João", "joao@email.com", "hash", null);
        user.setId("id-1");
        when(userService.registrar(any(UserDTO.class))).thenReturn(user);

        mockMvc.perform(post("/register")
                .param("nome", "João")
                .param("email", "joao@email.com")
                .param("senha", "12345678")
                .param("confirmacaoSenha", "12345678"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?registered"));
    }

    @Test
    @DisplayName("RF-01 RN-04: POST /register com email duplicado deve mostrar erro")
    void deveMostrarErroParaEmailDuplicado() throws Exception {
        when(userService.registrar(any(UserDTO.class)))
            .thenThrow(new EmailDuplicadoException("Email já cadastrado"));

        mockMvc.perform(post("/register")
                .param("nome", "João")
                .param("email", "duplicado@email.com")
                .param("senha", "12345678")
                .param("confirmacaoSenha", "12345678"))
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().attributeExists("erro"));
    }

    @Test
    @DisplayName("RF-11: POST /register com validação inválida deve mostrar erros")
    void deveMostrarErrosValidacao() throws Exception {
        when(userService.registrar(any(UserDTO.class)))
            .thenThrow(new ValidationException(List.of("Nome é obrigatório")));

        mockMvc.perform(post("/register")
                .param("nome", "")
                .param("email", "x")
                .param("senha", "123")
                .param("confirmacaoSenha", "456"))
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().attributeExists("erros"));
    }
}
