package com.biblioteca.service;

import com.biblioteca.dto.EnderecoInfo;
import com.biblioteca.exception.ApiIndisponivelException;
import com.biblioteca.exception.CepInvalidoException;
import com.biblioteca.exception.CepNaoEncontradoException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Testes de integração do CepLookupService com WireMock.
 * Padrão VCR/Record & Playback (RNF-01 seção 5.5).
 * Simula a API ViaCEP sem depender de rede.
 */
class CepLookupServiceIT {

    private static WireMockServer wireMock;
    private CepLookupService service;

    @BeforeAll
    static void startWireMock() {
        wireMock = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMock.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMock.stop();
    }

    @BeforeEach
    void setUp() {
        wireMock.resetAll();
        String baseUrl = "http://localhost:" + wireMock.port() + "/ws/";
        service = new CepLookupService(new RestTemplate(), baseUrl);
    }

    @Test
    @DisplayName("RF-10: Deve retornar endereço para CEP válido")
    void deveRetornarEnderecoParaCepValido() {
        wireMock.stubFor(get(urlEqualTo("/ws/01001000/json/"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "logradouro": "Praça da Sé",
                      "bairro": "Sé",
                      "localidade": "São Paulo",
                      "uf": "SP"
                    }
                    """)));

        EnderecoInfo resultado = service.buscarPorCep("01001000");

        assertThat(resultado.getLogradouro()).isEqualTo("Praça da Sé");
        assertThat(resultado.getBairro()).isEqualTo("Sé");
        assertThat(resultado.getLocalidade()).isEqualTo("São Paulo");
        assertThat(resultado.getUf()).isEqualTo("SP");
    }

    @Test
    @DisplayName("RF-10: Deve aceitar CEP com formatação (hífen)")
    void deveAceitarCepComHifen() {
        wireMock.stubFor(get(urlEqualTo("/ws/01001000/json/"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"logradouro\":\"Rua\",\"bairro\":\"B\",\"localidade\":\"C\",\"uf\":\"SP\"}")));

        EnderecoInfo resultado = service.buscarPorCep("01001-000");
        assertThat(resultado.getLogradouro()).isEqualTo("Rua");
    }

    @Test
    @DisplayName("RF-10 CA-04: Deve lançar CepNaoEncontradoException para CEP inexistente")
    void deveLancarExcecaoParaCepInexistente() {
        wireMock.stubFor(get(urlEqualTo("/ws/99999999/json/"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"erro\": true}")));

        assertThatThrownBy(() -> service.buscarPorCep("99999999"))
            .isInstanceOf(CepNaoEncontradoException.class)
            .hasMessage("CEP não encontrado");
    }

    @Test
    @DisplayName("RF-11 CA-03: Deve lançar CepInvalidoException para CEP com menos de 8 dígitos")
    void deveLancarExcecaoParaCepInvalido() {
        assertThatThrownBy(() -> service.buscarPorCep("1234"))
            .isInstanceOf(CepInvalidoException.class)
            .hasMessage("CEP deve ter 8 dígitos");
    }

    @Test
    @DisplayName("RF-10 CA-07: Deve lançar ApiIndisponivelException quando API está fora")
    void deveLancarExcecaoParaApiIndisponivel() {
        wireMock.stubFor(get(urlEqualTo("/ws/01001000/json/"))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Internal Server Error")));

        assertThatThrownBy(() -> service.buscarPorCep("01001000"))
            .isInstanceOf(ApiIndisponivelException.class);
    }

    @Test
    @DisplayName("RF-10: Deve lançar ApiIndisponivelException para timeout")
    void deveLancarExcecaoParaTimeout() {
        wireMock.stubFor(get(urlEqualTo("/ws/01001000/json/"))
            .willReturn(aResponse()
                .withFixedDelay(10000)
                .withStatus(200)));

        assertThatThrownBy(() -> service.buscarPorCep("01001000"))
            .isInstanceOf(ApiIndisponivelException.class);
    }
}
