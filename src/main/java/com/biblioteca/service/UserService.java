package com.biblioteca.service;

import com.biblioteca.dto.UserDTO;
import com.biblioteca.exception.EmailDuplicadoException;
import com.biblioteca.exception.ValidationException;
import com.biblioteca.model.Endereco;
import com.biblioteca.model.User;
import com.biblioteca.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de usuários — lógica de cadastro e autenticação.
 * Implementa UserDetailsService para integração com Spring Security (RF-02 seção 6).
 */
@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final String EMAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra um novo usuário no sistema.
     * Valida campos, verifica unicidade do email, hasheia senha e persiste.
     * Conforme RF-01.
     */
    public User registrar(UserDTO dto) {
        List<String> erros = new ArrayList<>();

        // RN-03: Nome não pode ser vazio
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            erros.add("Nome é obrigatório");
        }

        // RN-01: Email deve seguir formato válido
        if (dto.getEmail() == null || !dto.getEmail().matches(EMAIL_REGEX)) {
            erros.add("Email inválido");
        }

        // CA-03: Senha mínimo 8 caracteres
        if (dto.getSenha() == null || dto.getSenha().length() < 8) {
            erros.add("Senha deve ter no mínimo 8 caracteres");
        }

        // RN-02: Senha e confirmação devem ser iguais
        if (dto.getSenha() != null && !dto.getSenha().equals(dto.getConfirmacaoSenha())) {
            erros.add("Senhas não conferem");
        }

        if (!erros.isEmpty()) {
            throw new ValidationException(erros);
        }

        // RN-04: Email deve ser único
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailDuplicadoException("Email já cadastrado");
        }

        // CA-04: Hashear senha com BCrypt
        String senhaHash = passwordEncoder.encode(dto.getSenha());

        Endereco endereco = new Endereco(
            dto.getCep(), dto.getLogradouro(), dto.getBairro(),
            dto.getCidade(), dto.getUf(), dto.getNumero(), dto.getComplemento()
        );

        User user = new User(dto.getNome(), dto.getEmail(), senhaHash, endereco);
        user.setDataCriacao(LocalDateTime.now());

        log.info("Registrando novo usuário: {}", dto.getEmail());
        return userRepository.save(user);
    }

    /**
     * Carrega usuário por email para autenticação do Spring Security.
     * Conforme RF-02 seção 6.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas"));
    }
}
