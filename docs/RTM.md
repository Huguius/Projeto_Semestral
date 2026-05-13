# 📋 Matriz de Rastreabilidade Requisito → Teste (RTM)

Conforme **RNF-07** — garantia de que cada requisito funcional e não-funcional possui ao menos um teste automatizado associado.

## Requisitos Funcionais

| Requisito | Descrição | Classe(s) de Teste | Método(s) |
|-----------|-----------|--------------------|-----------| 
| RF-01 | Cadastro de usuário | `UserServiceTest` | `deveRegistrarUsuarioValido`, `deveRejeitarNomeVazio`, `deveRejeitarEmailInvalido`, `deveRejeitarSenhaCurta`, `deveRejeitarSenhasDiferentes`, `deveRejeitarEmailDuplicado` |
| RF-01 | Cadastro — endereço | `UserServiceTest` | `deveSalvarEnderecoCompleto` (via `registrar`) |
| RF-01 | Cadastro — BCrypt | `UserServiceTest` | `deveHashearSenha` |
| RF-01 | Cadastro — Controller | `AuthControllerTest`, `AuthControllerRegisterTest` | `registerDeveRetornarPagina`, `deveRedirecionarParaLoginAposCadastro`, `deveMostrarErroParaEmailDuplicado`, `deveMostrarErrosValidacao` |
| RF-02 | Login | `UserServiceTest`, `AuthControllerTest` | `deveCarregarPorEmail`, `deveLancarExcecaoEmailNaoEncontrado`, `loginDeveRetornarPagina` |
| RF-03 | Logout | `AuthControllerTest` | Coberto pela segurança (redirect) |
| RF-04 | Cadastro de livro | `BookServiceTest`, `BookControllerTest` | `deveCriarLivroComDadosValidos`, `deveUsarStatusPadraoQuandoNaoInformado`, `deveCriarLivroERedirecionar` |
| RF-05 | Listagem de livros | `BookServiceTest`, `BookRepositoryIT`, `BookControllerTest` | `deveListarLivrosDoUsuario`, `deveListarLivrosDoUsuarioOrdenadosPorDataDesc`, `deveListarLivros` |
| RF-06 | Busca de livros | `BookServiceTest`, `BookRepositoryIT`, `BookControllerTest` | `queryVaziaDeveRetornarTodos`, `queryNaoVaziaDeveUsarSearch`, `deveBuscarPorTituloCaseInsensitive`, `deveBuscarPorAutorCaseInsensitive`, `deveBuscarLivros` |
| RF-07 | Edição de livro | `BookServiceTest`, `BookControllerTest` | `deveAtualizarLivroComDadosValidos`, `deveAtualizarDataAtualizacaoEManterDataCadastro`, `deveExibirFormularioEdicao`, `deveAtualizarERedirecionar`, `deveMostrarErrosValidacaoAoAtualizar` |
| RF-08 | Exclusão de livro | `BookServiceTest`, `BookControllerTest` | `deveExcluirLivroDoProprioUsuario`, `deveNegarExclusaoDeOutroUsuario`, `deveExcluirERedirecionar` |
| RF-09 | Detalhes do livro | `BookServiceTest`, `BookControllerTest` | `deveRetornarLivroDoProprioUsuario`, `deveLancarExcecaoQuandoLivroNaoExiste`, `deveExibirDetalhes` |
| RF-10 | Consulta CEP (ViaCEP) | `CepLookupServiceTest`, `CepLookupServiceIT`, `CepControllerTest` | `deveRetornarEnderecoParaCepValido`, `deveAceitarCepComHifen`, `deveLancarExcecaoParaCepInexistente`, `deveLancarExcecaoParaApiIndisponivel`, `deveLancarExcecaoParaTimeout`, `deveRetornar200ParaCepValido`, `deveRetornar400ParaCepInvalido`, `deveRetornar404ParaCepNaoEncontrado`, `deveRetornar503ParaApiIndisponivel` |
| RF-11 | Validações | `BookValidatorTest`, `BookValidationParamTest`, `CepFormatParamTest` | Múltiplos métodos parametrizados |
| — | Página inicial | `HomeControllerTest` | `deveRedirecionarParaBooksQuandoAutenticado`, `deveRedirecionarParaLoginQuandoNaoAutenticado` |
| — | Exceções globais | `GlobalExceptionHandlerTest` | `deveRetornar404ParaLivroNaoEncontrado`, `deveRetornar403ParaAccessDenied` |
| — | Modelos (User, Book, Endereco) | `ModelTest` | `userDeveImplementarUserDetails`, `userConstrutorVazio`, `bookConstrutorComArgs`, `bookConstrutorVazioESetters`, `enderecoGettersSetters`, `statusLeituraDescricao` |
| — | DTOs (BookDTO, UserDTO, EnderecoInfo) | `DtoTest` | `userDtoGettersSetters`, `bookDtoGettersSetters`, `bookDtoConstrutorComArgs`, `enderecoInfoGettersSetters`, `enderecoInfoConstrutorComArgs` |
| — | Exceções customizadas | `ExceptionTest` | `validationException`, `emailDuplicadoException`, `cepInvalidoException`, `cepNaoEncontradoException`, `apiIndisponivelException`, `livroNaoEncontradoException` |

## Requisitos Não-Funcionais

| Requisito | Descrição | Evidência |
|-----------|-----------|-----------|
| RNF-01 | Testabilidade (>80% cobertura) | JaCoCo `mvn verify` — **99.3% atingido** (`target/site/jacoco/`) |
| RNF-02 | Quality Gate (Sonar) | `sonar-project.properties` + `.github/workflows/ci.yml` |
| RNF-03 | CI/CD | `.github/workflows/ci.yml` — build, test, sonar |
| RNF-04 | Responsividade | Bootstrap 5.3 + `style.css` (media queries) |
| RNF-05 | Segurança | `SecurityConfig.java`, `BookServiceTest.deveLancarAccessDeniedParaOutroUsuario` |
| RNF-06 | Performance | Índices MongoDB em `Book.java` (@CompoundIndex) |
| RNF-07 | Rastreabilidade | Este documento (RTM.md) |
| RNF-08 | Manutenibilidade | Separação MVC, BookValidator isolado, DTOs, GlobalExceptionHandler |

## Cobertura por Tipo de Teste

| Tipo | Quantidade | Classes |
|------|-----------|---------| 
| Unitário (Service) | ~30 testes | `BookServiceTest`, `UserServiceTest`, `BookValidatorTest`, `CepLookupServiceTest` |
| Unitário (Controller) | ~20 testes | `AuthControllerTest`, `AuthControllerRegisterTest`, `BookControllerTest`, `CepControllerTest`, `HomeControllerTest` |
| Unitário (Model/DTO/Exception) | ~20 testes | `ModelTest`, `DtoTest`, `ExceptionTest`, `GlobalExceptionHandlerTest` |
| Parametrizado | ~28 testes | `BookValidationParamTest`, `CepFormatParamTest` |
| Integração (Testcontainers) | ~10 testes | `BookRepositoryIT`, `UserRepositoryIT` |
| Integração (WireMock) | ~6 testes | `CepLookupServiceIT` |
| **Total** | **114 testes** | |

## Cobertura JaCoCo por Pacote

| Pacote | Cobertura |
|--------|-----------|
| `com.biblioteca.service` | 97.7% |
| `com.biblioteca.controller` | 100% |
| `com.biblioteca.model` | 100% |
| `com.biblioteca.dto` | 100% |
| `com.biblioteca.exception` | 100% |
| **Total** | **99.3%** |

> **Nota:** Classes de infraestrutura (`BibliotecaApplication`, `SecurityConfig`, `WebClientConfig`) são excluídas do JaCoCo por serem configuração pura.
