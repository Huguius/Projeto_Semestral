# 📋 Matriz de Rastreabilidade Requisito → Teste (RTM)

Conforme **RNF-07** — garantia de que cada requisito funcional e não-funcional possui ao menos um teste automatizado associado.

## Requisitos Funcionais

| Requisito | Descrição | Classe(s) de Teste | Método(s) |
|-----------|-----------|--------------------|-----------| 
| RF-01 | Cadastro de usuário | `UserServiceTest` | `deveRegistrarUsuarioValido`, `deveRejeitarNomeVazio`, `deveRejeitarEmailInvalido`, `deveRejeitarSenhaCurta`, `deveRejeitarSenhasDiferentes`, `deveRejeitarEmailDuplicado` |
| RF-01 | Cadastro — endereço | `UserServiceTest` | `deveSalvarEnderecoCompleto` (via `registrar`) |
| RF-01 | Cadastro — BCrypt | `UserServiceTest` | `deveHashearSenha` |
| RF-02 | Login | `UserServiceTest`, `AuthControllerTest` | `deveCarregarPorEmail`, `deveLancarExcecaoEmailNaoEncontrado`, `loginDeveRetornarPagina` |
| RF-03 | Logout | `AuthControllerTest` | Coberto pela segurança (redirect) |
| RF-04 | Cadastro de livro | `BookServiceTest`, `BookControllerTest` | `deveCriarLivroComDadosValidos`, `deveUsarStatusPadraoQuandoNaoInformado`, `deveCriarLivroERedirecionarComCSRF` |
| RF-05 | Listagem de livros | `BookServiceTest`, `BookRepositoryIT`, `BookControllerTest` | `deveListarLivrosDoUsuario`, `deveListarLivrosDoUsuarioOrdenadosPorDataDesc`, `deveListarLivros` |
| RF-06 | Busca de livros | `BookServiceTest`, `BookRepositoryIT`, `BookControllerTest` | `queryVaziaDeveRetornarTodos`, `queryNaoVaziaDeveUsarSearch`, `deveBuscarPorTituloCaseInsensitive`, `deveBuscarPorAutorCaseInsensitive`, `deveBuscarLivros` |
| RF-07 | Edição de livro | `BookServiceTest` | `deveAtualizarLivroComDadosValidos`, `deveAtualizarDataAtualizacaoEManterDataCadastro` |
| RF-08 | Exclusão de livro | `BookServiceTest`, `BookControllerTest` | `deveExcluirLivroDoProprioUsuario`, `deveNegarExclusaoDeOutroUsuario`, `deveExcluirERedirecionarComCSRF` |
| RF-09 | Detalhes do livro | `BookServiceTest`, `BookControllerTest` | `deveRetornarLivroDoProprioUsuario`, `deveLancarExcecaoQuandoLivroNaoExiste`, `deveExibirDetalhes` |
| RF-10 | Consulta CEP (ViaCEP) | `CepLookupServiceIT` | `deveRetornarEnderecoParaCepValido`, `deveAceitarCepComHifen`, `deveLancarExcecaoParaCepInexistente`, `deveLancarExcecaoParaApiIndisponivel`, `deveLancarExcecaoParaTimeout` |
| RF-11 | Validações | `BookValidatorTest`, `BookValidationParamTest`, `CepFormatParamTest` | Múltiplos métodos parametrizados |

## Requisitos Não-Funcionais

| Requisito | Descrição | Evidência |
|-----------|-----------|-----------|
| RNF-01 | Testabilidade (>80% cobertura) | JaCoCo `mvn verify` + relatório `target/site/jacoco/` |
| RNF-02 | Quality Gate (Sonar) | `sonar-project.properties` + `.github/workflows/ci.yml` |
| RNF-03 | CI/CD | `.github/workflows/ci.yml` — build, test, sonar |
| RNF-04 | Responsividade | Bootstrap 5.3 + `style.css` (media queries) |
| RNF-05 | Segurança | `SecurityConfig.java`, `BookControllerTest.postSemCsrfDeveSerRejeitado`, `AuthControllerTest.acessoSemAutenticacaoDeveRedirecionarParaLogin`, `BookServiceTest.deveLancarAccessDeniedParaOutroUsuario` |
| RNF-06 | Performance | Índices MongoDB em `Book.java` (@CompoundIndex) |
| RNF-07 | Rastreabilidade | Este documento (RTM.md) |
| RNF-08 | Manutenibilidade | Separação MVC, BookValidator isolado, DTOs, GlobalExceptionHandler |

## Cobertura por Tipo de Teste

| Tipo | Quantidade | Classes |
|------|-----------|---------|
| Unitário | ~30 testes | `BookServiceTest`, `UserServiceTest`, `BookValidatorTest` |
| Parametrizado | ~18 testes | `BookValidationParamTest`, `CepFormatParamTest` |
| Integração (Testcontainers) | ~10 testes | `BookRepositoryIT`, `UserRepositoryIT` |
| Integração (WireMock) | ~6 testes | `CepLookupServiceIT` |
| Controller (MockMvc) | ~10 testes | `AuthControllerTest`, `BookControllerTest` |
| **Total** | **~74 testes** | |
