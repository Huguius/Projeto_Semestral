# 📚 Biblioteca Pessoal — Documento de Planejamento

> **Disciplina:** Qualidade de Software  
> **Equipe:** Trio  
> **Abordagem:** Spec-Driven Development  
> **Data:** Maio/2026

---

## 1. Visão Geral do Projeto

Aplicação web completa para **cadastro e gerenciamento de livros** de uma biblioteca pessoal, com **autenticação de usuários**. O foco principal é **qualidade e testabilidade**, não apenas funcionalidade.

### Escopo
- CRUD completo de livros
- Cadastro e autenticação de usuários
- Gerenciamento de sessão
- Interface web responsiva
- Integração com API externa (API dos Correios / ViaCEP para consulta de endereço por CEP)
- Pipeline de qualidade automatizado

---

## 2. Requisitos Funcionais (RF)

| ID | Requisito | Descrição | Prioridade |
|----|-----------|-----------|------------|
| **RF-01** | Cadastro de Usuário | Criar conta com nome, email e senha | Alta |
| **RF-02** | Login | Autenticação via email/senha com criação de sessão | Alta |
| **RF-03** | Logout | Encerrar sessão do usuário | Alta |
| **RF-04** | Criar Livro | Cadastrar livro com título, autor, ISBN, gênero, ano e status de leitura | Alta |
| **RF-05** | Listar Livros | Exibir todos os livros do usuário autenticado | Alta |
| **RF-06** | Buscar Livro | Filtrar livros por título, autor ou gênero | Média |
| **RF-07** | Editar Livro | Atualizar dados de um livro existente | Alta |
| **RF-08** | Excluir Livro | Remover livro da biblioteca | Alta |
| **RF-09** | Detalhes do Livro | Visualizar informações completas de um livro | Média |
| **RF-10** | Consulta de CEP (API) | Preencher endereço do usuário automaticamente via API dos Correios (ViaCEP) no cadastro | Média |
| **RF-11** | Validação de Dados | Validar campos obrigatórios e formatos (ex: ISBN) | Alta |

> [!IMPORTANT]
> O **RF-10** (Consulta de CEP) existe para justificar o uso de **WireMock/VCR** — sem uma chamada a API externa, não haveria motivo para essa ferramenta. A API dos Correios (via ViaCEP) retorna logradouro, bairro, cidade e estado a partir do CEP, otimizando o cadastro do usuário.

---

## 3. Requisitos Não-Funcionais (RNF)

| ID | Requisito | Descrição | Métrica | Ferramenta de Verificação |
|----|-----------|-----------|---------|---------------------------|
| **RNF-01** | Testabilidade | Cobertura mínima de 80% | ≥ 80% linhas cobertas | JaCoCo |
| **RNF-02** | Qualidade de Código | Zero bugs críticos, zero vulnerabilidades | Quality Gate pass | SonarQube |
| **RNF-03** | CI/CD | Build e testes automatizados a cada push | Pipeline verde | GitHub Actions |
| **RNF-04** | Responsividade | Interface funcional em desktop e mobile | Layouts adaptáveis | Testes manuais + CSS |
| **RNF-05** | Segurança | Senhas hasheadas, sessão segura | BCrypt + HttpOnly cookies | Spring Security |
| **RNF-06** | Performance | Resposta da API < 500ms | Tempo de resposta | Testes de integração |
| **RNF-07** | Rastreabilidade | 100% dos RFs mapeados a testes | RTM completa | RTM.md |
| **RNF-08** | Manutenibilidade | Código limpo, padrão MVC | Baixo acoplamento | SonarQube + Code Review |

### Como RNFs conversam com RFs (importante para a oral)

```
RF-01 (Cadastro) ──► RNF-05 (Segurança): senha deve ser hasheada com BCrypt
RF-02 (Login)    ──► RNF-05 (Segurança): sessão com cookie HttpOnly
RF-04 (Criar)    ──► RNF-01 (Testabilidade): testado com Testcontainers (banco real)
RF-10 (CEP API)  ──► RNF-01 (Testabilidade): testado com WireMock/VCR (sem mock)
RF-11 (Validação)──► RNF-01 (Testabilidade): testes parametrizados (@CsvSource)
TODOS os RFs     ──► RNF-07 (Rastreabilidade): mapeados no RTM.md
TODOS os RFs     ──► RNF-03 (CI/CD): validados automaticamente no pipeline
```

---

## 4. Stack Tecnológica — Ferramentas e Justificativas

> [!TIP]
> Cada escolha abaixo inclui **"Por que SIM"** e **"Por que NÃO a alternativa"** — exatamente o que será perguntado na chamada oral.

### 4.1 Backend: Spring Boot 3.x (Java 17+)

| | Detalhe |
|---|---------|
| **Por que SIM** | Framework maduro, ecossistema vasto (Security, Data, Web), suporte nativo a Testcontainers desde v3.1, convenção sobre configuração reduz boilerplate |
| **Por que não Node.js/Express** | Java oferece tipagem forte (menos bugs em runtime), ecossistema de testes mais maduro para o contexto acadêmico (JUnit 5, JaCoCo, SonarQube) |
| **Por que não Quarkus/Micronaut** | Spring Boot é o padrão da indústria e da academia, maior base de documentação, o projeto não exige tempos de startup ultra-rápidos |

### 4.2 Banco de Dados: MongoDB 7.0

| | Detalhe |
|---|---------|
| **Por que SIM** | Modelo de documento JSON se alinha naturalmente com a entidade "Livro" (metadados variáveis: tags, notas, edições). Schema flexível permite evolução sem migrações. Integração nativa com Spring Data MongoDB |
| **Por que não PostgreSQL** | Embora robusto, o modelo relacional exigiria JOINs desnecessários para um domínio simples. MongoDB simplifica o mapeamento objeto→documento. O projeto não tem relacionamentos N:N complexos |
| **Por que não H2 (in-memory)** | H2 é SQL e não reflete o banco real em testes. Com Testcontainers, usamos MongoDB real nos testes — eliminando discrepâncias |
| **Conexão com RNF** | Suporta RNF-01 (Testcontainers roda MongoDB real) e RNF-06 (consultas rápidas em documentos) |

### 4.3 Arquitetura: MVC (Model-View-Controller)

| | Detalhe |
|---|---------|
| **Por que SIM** | Separação clara de responsabilidades (Controller→Service→Repository), alinhamento direto com Spring Boot, ideal para CRUD com complexidade moderada |
| **Por que não Hexagonal/Clean** | Over-engineering para o escopo do projeto. Hexagonal adiciona camadas de abstração (Ports, Adapters) que não se justificam em um CRUD. MVC já garante baixo acoplamento com menos boilerplate |
| **Por que não Monolítico sem camadas** | Violaria princípios SOLID, dificultaria testabilidade e manutenção |

### 4.4 Frontend: Thymeleaf (Server-Side Rendering)

| | Detalhe |
|---|---------|
| **Por que SIM** | Integração nativa com Spring Boot (mesmo projeto, mesmo deploy), gerenciamento de sessão simplificado via `HttpSession` + cookies, sem necessidade de API REST separada + CORS + JWT |
| **Por que não React/Angular (SPA)** | Adicionaria complexidade arquitetural (2 projetos separados, JWT, CORS), foge do foco em qualidade do backend. Thymeleaf mantém o foco no que importa: testes e qualidade |
| **Por que não JSP** | Tecnologia legada, Thymeleaf é o padrão moderno do Spring com "natural templates" (HTML válido mesmo sem servidor) |
| **Responsividade** | Bootstrap 5 para CSS responsivo — amplamente documentado, sem necessidade de build tools para CSS |

### 4.5 Testes de Persistência: Testcontainers

| | Detalhe |
|---|---------|
| **Por que SIM** | Roda uma instância **real** de MongoDB via Docker durante os testes. Elimina discrepâncias entre teste e produção. Atende à regra de "zero mocks" |
| **Por que não banco embarcado (Flapdoodle/Embedded Mongo)** | Flapdoodle foi descontinuado e não suporta MongoDB 7.0+. Embedded databases não replicam 100% do comportamento real |
| **Por que não mocks do Repository** | **Proibido pelo professor.** Além disso, mocks não validam queries reais contra o banco |

### 4.6 Testes de API Externa: WireMock (padrão VCR)

| | Detalhe |
|---|---------|
| **Por que SIM** | Implementa o padrão VCR (Record & Playback): grava chamadas reais à API dos Correios (ViaCEP) em arquivos JSON ("cassettes") e reproduz nas execuções seguintes. Testes determinísticos sem depender de rede |
| **Por que não Mockito/mocks manuais** | **Proibido pelo professor.** WireMock roda um servidor HTTP real, não é um mock — é um stub baseado em gravações reais |
| **Por que não OkHttp MockWebServer** | WireMock tem integração nativa com Spring Boot (`wiremock-spring-boot`), suporte a recording, e ecossistema mais maduro |
| **Conexão com RF** | Viabiliza RF-10 (consulta CEP) sem depender de internet nos testes |

### 4.7 Cobertura: JaCoCo

| | Detalhe |
|---|---------|
| **Por que SIM** | Padrão da indústria Java para cobertura, integração nativa com Maven e SonarQube, gera relatórios XML/HTML |
| **Por que não Cobertura (ferramenta)** | Projeto abandonado, não suporta Java 17+. JaCoCo é mantido ativamente |

### 4.8 Qualidade: SonarQube (via SonarCloud)

| | Detalhe |
|---|---------|
| **Por que SIM** | Análise estática de código (bugs, vulnerabilidades, code smells), integração com JaCoCo para visualizar cobertura, Quality Gates configuráveis |
| **Por que SonarCloud e não self-hosted** | SonarCloud é gratuito para projetos open-source, sem necessidade de infraestrutura própria, integração direta com GitHub |
| **Por que não apenas Checkstyle/PMD** | SonarQube engloba tudo (Checkstyle, PMD, FindBugs) em uma dashboard única com histórico |

### 4.9 CI/CD: GitHub Actions

| | Detalhe |
|---|---------|
| **Por que SIM** | Integrado ao GitHub (onde o repositório já está), gratuito para projetos públicos, suporte nativo a Docker (necessário para Testcontainers) |
| **Por que não Jenkins** | Exige servidor próprio. GitHub Actions é serverless e zero config de infra |
| **Por que não GitLab CI** | O repositório está no GitHub — usar GitLab CI exigiria migração ou mirror |

### 4.10 Build: Maven

| | Detalhe |
|---|---------|
| **Por que SIM** | Gerenciamento de dependências declarativo (pom.xml), ciclo de vida padronizado (`clean → compile → test → package`), integração nativa com JaCoCo e SonarQube |
| **Por que não Gradle** | Maven é mais verboso porém mais previsível e amplamente documentado em contextos acadêmicos. Gradle tem curva de aprendizado maior com Groovy/Kotlin DSL |

---

## 5. Estrutura do Projeto

```
biblioteca-pessoal/
├── .github/
│   └── workflows/
│       └── ci.yml                    # Pipeline GitHub Actions
├── src/
│   ├── main/
│   │   ├── java/com/biblioteca/
│   │   │   ├── BibliotecaApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java       # Spring Security + BCrypt
│   │   │   │   └── WebClientConfig.java      # RestTemplate (timeouts API ViaCEP)
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java       # Login/Registro
│   │   │   │   ├── BookController.java       # CRUD Livros
│   │   │   │   ├── CepController.java        # REST API consulta CEP
│   │   │   │   └── HomeController.java       # Página inicial (redirect)
│   │   │   ├── dto/
│   │   │   │   ├── BookDTO.java
│   │   │   │   ├── UserDTO.java
│   │   │   │   └── EnderecoInfo.java         # Resposta da API ViaCEP
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java  # @ControllerAdvice
│   │   │   │   ├── ValidationException.java
│   │   │   │   ├── EmailDuplicadoException.java
│   │   │   │   ├── LivroNaoEncontradoException.java
│   │   │   │   ├── CepInvalidoException.java
│   │   │   │   ├── CepNaoEncontradoException.java
│   │   │   │   └── ApiIndisponivelException.java
│   │   │   ├── model/
│   │   │   │   ├── User.java                 # Entidade Usuário (UserDetails)
│   │   │   │   ├── Book.java                 # Entidade Livro
│   │   │   │   ├── Endereco.java             # Documento embarcado
│   │   │   │   └── StatusLeitura.java        # Enum (QUERO_LER, LENDO, LIDO)
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java       # Spring Data Mongo
│   │   │   │   └── BookRepository.java
│   │   │   └── service/
│   │   │       ├── UserService.java          # Lógica de negócio
│   │   │       ├── BookService.java
│   │   │       ├── BookValidator.java        # Validações de livro
│   │   │       └── CepLookupService.java     # Chamada API ViaCEP
│   │   ├── resources/
│   │   │   ├── application.yml
│   │   │   ├── templates/                    # Thymeleaf
│   │   │   │   ├── login.html
│   │   │   │   ├── register.html
│   │   │   │   ├── books/
│   │   │   │   │   ├── list.html
│   │   │   │   │   ├── form.html
│   │   │   │   │   └── detail.html
│   │   │   │   └── error/
│   │   │   │       ├── 403.html
│   │   │   │       └── 404.html
│   │   │   └── static/
│   │   │       ├── css/style.css
│   │   │       └── js/app.js
│   ├── test/
│   │   └── java/com/biblioteca/
│   │       ├── AbstractIntegrationTest.java      # Base para testes de integração
│   │       ├── controller/                       # Testes de Controller (MockMvc)
│   │       │   ├── AuthControllerTest.java       # GET /login, GET /register
│   │       │   ├── AuthControllerRegisterTest.java # POST /register (sucesso, duplicado, validação)
│   │       │   ├── BookControllerTest.java       # CRUD completo + validações
│   │       │   ├── CepControllerTest.java        # REST CEP (200, 400, 404, 503)
│   │       │   ├── HomeControllerTest.java       # Redirect autenticado/não-autenticado
│   │       │   └── TestUserArgumentResolver.java # Helper para @AuthenticationPrincipal
│   │       ├── dto/                              # Testes de DTOs
│   │       │   └── DtoTest.java                  # Getters/setters/construtores
│   │       ├── exception/                        # Testes de Exceções
│   │       │   ├── ExceptionTest.java            # Todas as exceções customizadas
│   │       │   └── GlobalExceptionHandlerTest.java # 404 e 403
│   │       ├── model/                            # Testes de Modelos
│   │       │   └── ModelTest.java                # User, Book, Endereco, StatusLeitura
│   │       ├── repository/                       # Testes de Integração (Testcontainers)
│   │       │   ├── BookRepositoryIT.java         # CRUD real no MongoDB
│   │       │   └── UserRepositoryIT.java
│   │       └── service/                          # Testes de Serviço
│   │           ├── BookServiceTest.java          # Lógica de negócio
│   │           ├── BookValidatorTest.java         # Validações unitárias
│   │           ├── BookValidationParamTest.java   # @ParameterizedTest validações
│   │           ├── CepFormatParamTest.java        # @ParameterizedTest formato CEP
│   │           ├── CepLookupServiceTest.java      # Unitário (Mockito/RestTemplate)
│   │           ├── CepLookupServiceIT.java        # Integração (WireMock/VCR)
│   │           └── UserServiceTest.java
├── docs/
│   ├── RTM.md                                # Matriz de Rastreabilidade
│   └── requisitos/                           # Documentação de requisitos
│       ├── funcionais/
│       └── nao-funcionais/
├── README.md
├── pom.xml
├── sonar-project.properties
└── docker-compose.yml                        # MongoDB local
```

---

## 6. Estratégia de Testes (Detalhada)

### 6.1 Mapa de Tipos de Teste por Camada

| Camada | Tipo de Teste | Ferramenta | Exemplo |
|--------|---------------|------------|---------|
| **Repository** | Integração | Testcontainers + MongoDB | `BookRepositoryIT`: CRUD real no banco |
| **Service** | Caixa Branca (unitário) | JUnit 5 + Mockito | `BookServiceTest`: lógica de negócio, `CepLookupServiceTest`: API com RestTemplate mockado |
| **Service** | Parametrizado | JUnit 5 `@ParameterizedTest` | `BookValidationParamTest`: múltiplos cenários de validação, `CepFormatParamTest`: formatos CEP |
| **Service (API)** | Integração VCR | WireMock standalone | `CepLookupServiceIT`: replay de chamadas à API ViaCEP |
| **Controller** | Caixa Preta | MockMvc standalone | `BookControllerTest`, `AuthControllerTest`, `CepControllerTest`, `HomeControllerTest` |
| **Model/DTO/Exception** | Unitário | JUnit 5 | `ModelTest`, `DtoTest`, `ExceptionTest`, `GlobalExceptionHandlerTest` |

### 6.2 Exemplo Conceitual: Testes Parametrizados

```java
@ParameterizedTest(name = "CEP \"{0}\" → válido={1}")
@CsvSource({
    "01001-000, true",     // CEP válido (São Paulo - Praça da Sé)
    "80010-000, true",     // CEP válido (Curitiba)
    "00000-000, false",    // CEP inválido (zeros)
    "'', false",           // Vazio
    "1234, false",         // Muito curto
    "12345-6789, false",   // Formato inválido (dígitos extras)
    "ABCDE-FGH, false"    // Letras não permitidas
})
void deveValidarFormatoCep(String cep, boolean esperado) {
    assertEquals(esperado, validator.isValidCep(cep));
}
```

### 6.3 Exemplo Conceitual: Testcontainers (sem mocks)

```java
@Testcontainers
@SpringBootTest
class BookRepositoryIT {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private BookRepository bookRepository;

    @Test
    void deveSalvarERecuperarLivro() {
        Book livro = new Book("Dom Casmurro", "Machado de Assis", "978-...");
        bookRepository.save(livro);
        Optional<Book> encontrado = bookRepository.findByTitulo("Dom Casmurro");
        assertTrue(encontrado.isPresent());
    }
}
```

### 6.4 Exemplo Conceitual: WireMock/VCR

```java
@SpringBootTest
@EnableWireMock
class CepLookupServiceIT {

    @InjectWireMock
    private WireMockServer wireMock;

    // 1ª execução: grava chamada real → salva em /wiremock/mappings/
    // Execuções seguintes: reproduz do arquivo (sem rede)

    @Test
    void deveBuscarEnderecoPorCep() {
        // WireMock serve a resposta gravada da API dos Correios (ViaCEP)
        EnderecoInfo endereco = cepLookupService.buscarPorCep("01001-000");
        assertEquals("Praça da Sé", endereco.getLogradouro());
        assertEquals("Sé", endereco.getBairro());
        assertEquals("São Paulo", endereco.getLocalidade());
        assertEquals("SP", endereco.getUf());
    }
}
```

### 6.5 Como os Tipos de Teste Conversam (para a oral)

```
Testes Parametrizados ──► validam REGRAS DE NEGÓCIO (Caixa Branca)
         │                 com múltiplos cenários de entrada
         ▼
Testcontainers ──────► validam PERSISTÊNCIA REAL (Integração)
         │              sem mocks, com MongoDB real em Docker
         ▼
WireMock/VCR ────────► validam INTEGRAÇÕES EXTERNAS (Integração)
         │              com gravações reais, sem depender de rede
         ▼
Controller Tests ────► validam FLUXO COMPLETO (Caixa Preta / E2E)
         │              requisição HTTP → controller → service → banco
         ▼
JaCoCo ──────────────► MEDE tudo acima (≥ 80%)
         ▼
SonarQube ───────────► ANALISA qualidade além de cobertura
         ▼
GitHub Actions ──────► AUTOMATIZA tudo a cada push
```

---

## 7. Pipeline CI/CD (GitHub Actions)

```yaml
# .github/workflows/ci.yml (conceitual)
name: CI Pipeline
on: [push, pull_request]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '17', distribution: 'temurin', cache: 'maven' }

      - name: Build & Test (Testcontainers + WireMock)
        run: mvn clean verify

      - name: SonarCloud Analysis
        uses: SonarSource/sonarcloud-github-action@master
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

**Fluxo:** Push → Build → Testes (Testcontainers + WireMock) → JaCoCo Report → SonarCloud → Quality Gate

---

## 8. Documentação Obrigatória

### 8.1 RTM.md (Matriz de Rastreabilidade)

Cada RF deve ter:
- ID do requisito
- Descrição
- Classe(s) de teste que o cobrem
- Tipo de teste (unitário, integração, E2E, parametrizado)
- Status (coberto/pendente)
- Diagrama UML de sequência

Exemplo de entrada:

| RF | Descrição | Testes | Tipo | Status |
|----|-----------|--------|------|--------|
| RF-04 | Criar Livro | `BookRepositoryIT.deveSalvarLivro()`, `BookControllerTest.deveCriarLivro()`, `BookValidationParamTest` | Integração + E2E + Parametrizado | ✅ |

### 8.2 Diagramas UML de Sequência

Um diagrama para cada RF principal. Exemplo para RF-04 (Criar Livro):

```mermaid
sequenceDiagram
    actor U as Usuário
    participant C as BookController
    participant S as BookService
    participant V as BookValidator
    participant R as BookRepository
    participant DB as MongoDB

    U->>C: POST /books (título, autor, isbn)
    C->>S: criarLivro(bookDTO)
    S->>V: validar(bookDTO)
    V-->>S: válido
    S->>R: save(book)
    R->>DB: insert document
    DB-->>R: ok
    R-->>S: Book (com ID)
    S-->>C: Book salvo
    C-->>U: redirect /books (com flash message)
```

### 8.3 README.md

Deve conter:
- Descrição do projeto
- Tecnologias e justificativas (resumo)
- Pré-requisitos (Java 17, Docker, Maven)
- Como rodar (`docker-compose up` + `mvn spring-boot:run`)
- Como rodar os testes (`mvn verify`)
- Como ver cobertura (link JaCoCo)
- Link do SonarCloud
- Estrutura do projeto
- Membros da equipe

---

## 9. Dependências Maven (pom.xml)

```xml
<!-- Core -->
spring-boot-starter-web
spring-boot-starter-data-mongodb
spring-boot-starter-security
spring-boot-starter-thymeleaf
spring-boot-starter-validation
thymeleaf-extras-springsecurity6

<!-- Frontend -->
bootstrap (via WebJars) 5.3.3
webjars-locator-core

<!-- Testes -->
spring-boot-starter-test           <!-- JUnit 5, Mockito, AssertJ, MockMvc -->
spring-security-test
spring-boot-testcontainers
org.testcontainers:mongodb 1.20.4
org.testcontainers:junit-jupiter 1.20.4
wiremock-standalone 3.9.2          <!-- WireMock para VCR da API ViaCEP -->

<!-- Qualidade -->
jacoco-maven-plugin 0.8.12         <!-- Cobertura ≥ 80%, exclui classes de config -->
sonar-maven-plugin 4.0.0.4121
```

---

## 10. Divisão de Tarefas (Trio)

### Fase 1 — Setup (Semana 1)
| Tarefa | Responsável |
|--------|-------------|
| Criar repositório GitHub + branch strategy | Membro A |
| Inicializar projeto Spring Boot + pom.xml | Membro B |
| Configurar docker-compose.yml (MongoDB) | Membro C |
| Configurar GitHub Actions (CI básico) | Membro A |

### Fase 2 — Backend (Semanas 2-3)
| Tarefa | Responsável |
|--------|-------------|
| Model + Repository (User, Book) | Membro A |
| Service layer (BookService, UserService, Validator) | Membro B |
| Controller layer + Spring Security | Membro C |
| CepLookupService (API Correios / ViaCEP) | Membro B |

### Fase 3 — Frontend (Semana 3)
| Tarefa | Responsável |
|--------|-------------|
| Templates Thymeleaf (layout, home, login) | Membro C |
| Templates de livros (list, form, detail) | Membro A |
| CSS responsivo + UX | Membro C |

### Fase 4 — Testes (Semanas 4-5)
| Tarefa | Responsável |
|--------|-------------|
| Testes de integração - Repository (Testcontainers) | Membro A |
| Testes WireMock/VCR (CepLookupService) | Membro B |
| Testes parametrizados (validações) | Membro B |
| Testes caixa branca (Service) | Membro A |
| Testes caixa preta (Controller E2E) | Membro C |

### Fase 5 — Qualidade e Docs (Semana 5-6)
| Tarefa | Responsável |
|--------|-------------|
| Configurar JaCoCo + SonarCloud | Membro A |
| Pipeline CI completo (build+test+sonar) | Membro A |
| RTM.md com diagramas UML | Membro B |
| README.md detalhado | Membro C |
| Revisão de cobertura (≥ 80%) | Todos |

---

## 11. Checklist de Entrega

- [x] Repositório GitHub com histórico de commits organizado
- [x] Código-fonte completo (backend + frontend)
- [x] `README.md` detalhado
- [x] `RTM.md` com mapeamento de todos os RFs a testes
- [x] Cobertura JaCoCo ≥ 80% — **atingido 99.3%** (114 testes)
- [ ] SonarCloud configurado e Quality Gate passando
- [ ] GitHub Actions pipeline verde (build + test + sonar)
- [x] Testcontainers para persistência real (sem mock de Repository)
- [x] WireMock para integração com API ViaCEP (sem mock de HTTP)
- [x] Mockito apenas para dependências de serviço em testes de controller
- [x] Todos os RFs cobertos por pelo menos um teste
- [x] Testes parametrizados presentes (`BookValidationParamTest`, `CepFormatParamTest`)
- [x] Testes caixa branca (Service) e caixa preta (Controller/MockMvc) identificados

---

## Open Questions

> [!IMPORTANT]
> **Perguntas para alinhar com o trio antes de começar:**

1. **Nome do repositório GitHub** — sugestão: `biblioteca-pessoal` ou `personal-library`?
2. **Branch strategy** — `main` + `develop` + feature branches, ou apenas `main` + feature branches?
3. **SonarCloud vs SonarQube self-hosted?** — SonarCloud é mais simples (gratuito para open-source). O professor exige self-hosted?
4. **Escopo do frontend** — Apenas funcional ou querem investir em UX premium (animações, dark mode)?
5. **Distribuição dos membros** — A tabela da Seção 10 é uma sugestão. Quem fica com o quê?
6. **Campos de endereço no cadastro** — Quais campos de endereço devem ser preenchidos automaticamente via CEP? Sugestão: logradouro, bairro, cidade, UF (o usuário preenche apenas número e complemento).
