package com.biblioteca.controller;

import com.biblioteca.dto.UserDTO;
import com.biblioteca.exception.EmailDuplicadoException;
import com.biblioteca.exception.ValidationException;
import com.biblioteca.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller de autenticação — cadastro e login.
 * Conforme RF-01, RF-02, RF-03.
 */
@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Exibe o formulário de login.
     * Mensagens condicionais: ?error, ?registered, ?logout (RF-02 CA-06).
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Exibe o formulário de cadastro.
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    /**
     * Processa o cadastro de novo usuário.
     * Conforme RF-01 CA-07: redireciona para /login?registered com flash message.
     */
    @PostMapping("/register")
    public String registrar(@ModelAttribute UserDTO userDTO,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            userService.registrar(userDTO);
            log.info("Usuário registrado com sucesso: {}", userDTO.getEmail());
            redirectAttributes.addFlashAttribute("mensagem", "Cadastro realizado com sucesso!");
            return "redirect:/login?registered";
        } catch (EmailDuplicadoException e) {
            log.warn("Tentativa de cadastro com email duplicado: {}", userDTO.getEmail());
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("userDTO", userDTO);
            return "register";
        } catch (ValidationException e) {
            log.warn("Erro de validação no cadastro: {}", e.getErros());
            model.addAttribute("erros", e.getErros());
            model.addAttribute("userDTO", userDTO);
            return "register";
        }
    }
}
