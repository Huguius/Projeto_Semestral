# RNF-06 — Performance

> **Métrica:** Resposta da API < 500ms  
> **Ferramenta de Verificação:** Testes de integração  
> **Prioridade:** Média

---

## 1. Descrição

Todas as operações da aplicação devem ter tempo de resposta **inferior a 500ms**, medido do recebimento do request HTTP até o envio da resposta. Isso inclui operações de CRUD, autenticação e consulta de CEP.

---

## 2. Critérios de Verificação

| # | Critério | Tipo |
|---|----------|------|
| CV-01 | Operações CRUD de livros (criar, listar, editar, excluir) < 500ms | Obrigatório |
| CV-02 | Login e registro < 500ms (excluindo latência de rede do BCrypt) | Obrigatório |
| CV-03 | Consulta de CEP < 5000ms (timeout da API externa) | Obrigatório |
| CV-04 | Renderização de templates Thymeleaf < 200ms | Desejável |
| CV-05 | Índices no MongoDB para queries frequentes | Obrigatório |

---

## 3. Operações e Tempos Esperados

| Operação | Tempo esperado | Gargalo potencial |
|----------|---------------|-------------------|
| `POST /register` | < 300ms | BCrypt.encode() (~100ms com cost=10) |
| `POST /login` | < 300ms | BCrypt.matches() (~100ms) |
| `POST /books` | < 200ms | Insert no MongoDB |
| `GET /books` | < 200ms | Query `findByUserId()` |
| `GET /books?q=...` | < 300ms | Regex search no MongoDB |
| `GET /api/cep/{cep}` | < 5000ms | Latência da API ViaCEP (externa) |
| `PUT /books/{id}` | < 200ms | Update no MongoDB |
| `DELETE /books/{id}` | < 200ms | Delete no MongoDB |

---

## 4. Estratégias de Otimização

### 4.1 Índices no MongoDB

```java
// Na entidade Book
@Document(collection = "books")
@CompoundIndex(def = "{'userId': 1}")        // Índice para listagem por usuário
@CompoundIndex(def = "{'userId': 1, 'titulo': 1}")  // Índice para busca
public class Book {
    // ...
}

// Na entidade User
@Document(collection = "users")
@Indexed(unique = true)
private String email;  // Índice para login (findByEmail)
```

### 4.2 Timeout na API Externa

```java
@Bean
public RestTemplate restTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(3000);  // 3s para conectar
    factory.setReadTimeout(5000);     // 5s para resposta
    return new RestTemplate(factory);
}
```

### 4.3 Cache de Sessão

Spring Boot gerencia sessões em memória por padrão (`HttpSession`). Para o escopo deste projeto, isso é suficiente — não há necessidade de Redis ou cache distribuído.

---

## 5. Como Medir

```java
// Nos testes de integração, medir tempo de resposta
@Test
void deveListarLivrosEmMenosDe500ms() {
    long inicio = System.currentTimeMillis();

    ResponseEntity<String> response = restTemplate.getForEntity("/books", String.class);

    long duracao = System.currentTimeMillis() - inicio;
    assertTrue(duracao < 500, "Listagem demorou " + duracao + "ms (máximo: 500ms)");
    assertEquals(HttpStatus.OK, response.getStatusCode());
}
```

---

## 6. RFs Impactados

| RF | Operação | Tempo alvo |
|----|----------|-----------|
| **RF-01** | POST /register | < 300ms |
| **RF-02** | POST /login | < 300ms |
| **RF-04** | POST /books | < 200ms |
| **RF-05** | GET /books | < 200ms |
| **RF-06** | GET /books?q=... | < 300ms |
| **RF-07** | PUT /books/{id} | < 200ms |
| **RF-08** | DELETE /books/{id} | < 200ms |
| **RF-10** | GET /api/cep/{cep} | < 5000ms |

---

## 7. Conexão com outros RNFs

| RNF | Relação |
|-----|---------|
| **RNF-01 (Testabilidade)** | Testes de integração podem medir tempo de resposta |
| **RNF-05 (Segurança)** | BCrypt cost=10 impacta ~100ms no login/registro (trade-off aceito) |
| **RNF-08 (Manutenibilidade)** | Índices documentados facilitam manutenção |

> [!TIP]
> **Para a oral:** "O BCrypt com cost factor 10 leva ~100ms para hashear. Isso parece lento, mas é proposital — dificulta brute force. O trade-off é aceito porque 100ms é imperceptível para o usuário, mas torna ataques de força bruta impraticáveis."
