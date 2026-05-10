package com.biblioteca.repository;

import com.biblioteca.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Repository para operações de persistência de livros.
 * Conforme RF-04 a RF-09.
 */
public interface BookRepository extends MongoRepository<Book, String> {

    /**
     * Lista livros de um usuário, ordenados por data de cadastro (mais recente primeiro).
     * Conforme RF-05 CA-05.
     */
    List<Book> findByUserIdOrderByDataCadastroDesc(String userId);

    /**
     * Busca livros por query (título, autor ou gênero) com regex case-insensitive.
     * Filtra por userId para isolamento de dados (RNF-05 seção 3.4).
     * Conforme RF-06 seção 5.
     */
    @Query("{ 'userId': ?1, $or: [ " +
           "{ 'titulo': { $regex: ?0, $options: 'i' } }, " +
           "{ 'autor': { $regex: ?0, $options: 'i' } }, " +
           "{ 'genero': { $regex: ?0, $options: 'i' } } " +
           "] }")
    List<Book> searchByQuery(String query, String userId);
}
