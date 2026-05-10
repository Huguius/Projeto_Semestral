package com.biblioteca.repository;

import com.biblioteca.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository para operações de persistência de usuários.
 * Conforme RF-01, RF-02.
 */
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Busca um usuário pelo email.
     * Usado no login (RF-02) e na validação de email único (RF-01 RN-04).
     */
    Optional<User> findByEmail(String email);
}
