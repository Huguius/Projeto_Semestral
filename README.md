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
| Testes | JUnit 5, Testcontainers, WireMock, MockMvc |
| Cobertura | JaCoCo (≥ 80%) |
| CI/CD | GitHub Actions |
| Análise Estática | SonarCloud |

## 📋 Pré-requisitos

- Java 17+
- Maven 3.9+
- Docker Desktop (para testes com Testcontainers)

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
mvn verify
```

O relatório de cobertura estará em `target/site/jacoco/index.html`.

## 📁 Estrutura do Projeto

```
src/
├── main/java/com/biblioteca/
│   ├── config/          # SecurityConfig, WebClientConfig
│   ├── controller/      # AuthController, BookController, CepController
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
    ├── controller/      # AuthControllerTest, BookControllerTest
    ├── repository/      # BookRepositoryIT, UserRepositoryIT
    └── service/         # BookServiceTest, UserServiceTest, BookValidatorTest,
                         # BookValidationParamTest, CepFormatParamTest,
                         # CepLookupServiceIT
```

## 📊 Qualidade

| Métrica | Meta | Ferramenta |
|---------|------|-----------|
| Cobertura de código | ≥ 80% | JaCoCo |
| Quality Gate | Aprovado | SonarCloud |
| Rastreabilidade | 100% dos RFs | [RTM.md](docs/RTM.md) |

## 📖 Documentação

- [Objetivo Geral](ObjetivoGeral.md)
- [Requisitos Funcionais](docs/requisitos/funcionais/)
- [Requisitos Não-Funcionais](docs/requisitos/nao-funcionais/)
- [Matriz de Rastreabilidade](docs/RTM.md)
