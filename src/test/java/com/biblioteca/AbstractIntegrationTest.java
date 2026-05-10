package com.biblioteca;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Classe abstrata de testes de integração.
 * Provisiona MongoDB 7.0 via Testcontainers com container reutilizável (RNF-01 seção 3).
 * Todas as classes de integração devem estender esta classe.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Container
    static final MongoDBContainer mongoDBContainer =
        new MongoDBContainer("mongo:7.0")
            .withReuse(true);

    /**
     * Injeta a URI do MongoDB provisionado pelo Testcontainers
     * nas propriedades do Spring, substituindo a configuração padrão.
     */
    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
}
