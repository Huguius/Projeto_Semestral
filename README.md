# 📚 Biblioteca Pessoal

Aplicação web para gerenciamento de biblioteca pessoal com autenticação, cadastro de livros e consulta de CEP via ViaCEP.

**Projeto acadêmico** — Disciplina de Qualidade de Software (4º Semestre SENAC).

## 🛠️ Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| Backend | Spring Boot 3.3.5, Java 17 |
| Banco de Dados | MongoDB 7.0 |
| Frontend | Thymeleaf + Bootstrap 5.3 |
| Segurança | Spring Security + BCrypt |
| API Externa | ViaCEP (consulta de CEP) |
| Testes | JUnit 5, Testcontainers, WireMock, MockMvc, Mockito |
| Cobertura | JaCoCo (≥ 80% — atingido **99.3%**) |
| CI/CD | GitHub Actions (build + testes) |
| Análise Estática | SonarCloud (Automatic Analysis) |

## 📋 Pré-requisitos

- Java 17+
- Maven 3.9+
- Docker Desktop (para testes de integração com Testcontainers)

## 🚀 Como Executar

### 1. Subir o MongoDB
```bash
docker-compose up -d
```

### 2. Rodar a aplicação
```bash
mvn spring-boot:run
```

Acesse: **http://localhost:8080**

### 3. Rodar os testes

```bash
# Testes unitários + cobertura (sem Docker)
mvn clean verify -DskipITs

# Testes completos (unitários + integração — requer Docker)
mvn clean verify
```

O relatório de cobertura estará em `target/site/jacoco/index.html`.

## 📁 Estrutura do Projeto

```
src/
├── main/java/com/biblioteca/
│   ├── config/          # SecurityConfig, WebClientConfig
│   ├── controller/      # AuthController, BookController, CepController, HomeController
│   ├── dto/             # UserDTO, BookDTO, EnderecoInfo
│   ├── exception/       # Exceções customizadas + GlobalExceptionHandler
│   ├── model/           # User, Book, Endereco, StatusLeitura
│   ├── repository/      # UserRepository, BookRepository
│   └── service/         # UserService, BookService, BookValidator, CepLookupService
├── main/resources/
│   ├── templates/       # Thymeleaf (login, register, books/*)
│   ├── static/          # CSS, JS
│   └── application.yml
└── test/java/com/biblioteca/
    ├── controller/      # AuthControllerTest, AuthControllerRegisterTest,
    │                    # BookControllerTest, CepControllerTest, HomeControllerTest
    ├── dto/             # DtoTest (BookDTO, UserDTO, EnderecoInfo)
    ├── exception/       # ExceptionTest, GlobalExceptionHandlerTest
    ├── model/           # ModelTest (User, Book, Endereco, StatusLeitura)
    ├── repository/      # BookRepositoryIT, UserRepositoryIT (Testcontainers)
    └── service/         # BookServiceTest, UserServiceTest, BookValidatorTest,
                         # BookValidationParamTest, CepFormatParamTest,
                         # CepLookupServiceTest, CepLookupServiceIT (WireMock)
```

## 🧪 Tipos de Teste

| Tipo | Quantidade | Ferramenta | Classes |
|------|-----------|------------|---------|
| Unitário (Service) | ~30 | JUnit 5 + Mockito | `BookServiceTest`, `UserServiceTest`, `BookValidatorTest`, `CepLookupServiceTest` |
| Unitário (Controller) | ~20 | MockMvc standalone | `AuthControllerTest`, `AuthControllerRegisterTest`, `BookControllerTest`, `CepControllerTest`, `HomeControllerTest` |
| Unitário (Model/DTO/Exception) | ~20 | JUnit 5 | `ModelTest`, `DtoTest`, `ExceptionTest`, `GlobalExceptionHandlerTest` |
| Parametrizado | ~28 | `@ParameterizedTest` | `BookValidationParamTest`, `CepFormatParamTest` |
| Integração (Testcontainers) | ~10 | Testcontainers + MongoDB | `BookRepositoryIT`, `UserRepositoryIT` |
| Integração (WireMock) | ~6 | WireMock standalone | `CepLookupServiceIT` |
| **Total** | **~114** | | |

## 📊 Qualidade

| Métrica | Meta | Resultado | Ferramenta |
|---------|------|-----------|-----------|
| Cobertura de código | ≥ 80% | **99.3%** | JaCoCo |
| Quality Gate | Aprovado | ✅ | SonarCloud |
| Rastreabilidade | 100% dos RFs | ✅ | [RTM.md](docs/RTM.md) |
| Total de testes | — | 114 testes | JUnit 5 |

> **Nota:** Classes de infraestrutura (`BibliotecaApplication`, `SecurityConfig`, `WebClientConfig`) são excluídas da contagem do JaCoCo por serem configuração pura sem lógica de negócio.

### Pipeline de Verificação

O projeto utiliza **duas verificações automáticas independentes** a cada push:

| Verificação | Ferramenta | Responsabilidade |
|---|---|---|
| **CI** | GitHub Actions | Build, testes (Testcontainers + WireMock), cobertura JaCoCo ≥ 80% |
| **Análise estática** | SonarCloud (Automatic Analysis) | Bugs, code smells, vulnerabilidades, Quality Gate |

> O SonarCloud opera com **Automatic Analysis** habilitado, analisando o código diretamente do repositório de forma independente do CI.

## 📖 Documentação

- [Objetivo Geral](ObjetivoGeral.md)
- [Requisitos Funcionais](docs/requisitos/funcionais/)
- [Requisitos Não-Funcionais](docs/requisitos/nao-funcionais/)
- [Matriz de Rastreabilidade](docs/RTM.md)
