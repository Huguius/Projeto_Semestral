package com.biblioteca.service;

import com.biblioteca.dto.EnderecoInfo;
import com.biblioteca.exception.ApiIndisponivelException;
import com.biblioteca.exception.CepInvalidoException;
import com.biblioteca.exception.CepNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do CepLookupService.
 * Usa Mockito para simular o RestTemplate (sem necessidade de WireMock/rede).
 * Conforme RF-10.
 */
@ExtendWith(MockitoExtension.class)
class CepLookupServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private CepLookupService service;

    @BeforeEach
    void setUp() {
        service = new CepLookupService(restTemplate, "http://viacep.com.br/ws/");
    }

    @Test
    @DisplayName("RF-10: Deve retornar endereço para CEP válido")
    void deveRetornarEnderecoParaCepValido() {
        Map<String, Object> body = new HashMap<>();
        body.put("logradouro", "Praça da Sé");
        body.put("bairro", "Sé");
        body.put("localidade", "São Paulo");
        body.put("uf", "SP");

        when(restTemplate.getForObject(
            eq("http://viacep.com.br/ws/01001000/json/"), eq(Map.class)))
            .thenReturn(body);

        EnderecoInfo resultado = service.buscarPorCep("01001000");

        assertThat(resultado.getLogradouro()).isEqualTo("Praça da Sé");
        assertThat(resultado.getBairro()).isEqualTo("Sé");
        assertThat(resultado.getLocalidade()).isEqualTo("São Paulo");
        assertThat(resultado.getUf()).isEqualTo("SP");
    }

    @Test
    @DisplayName("RF-10: Deve aceitar CEP com formatação (hífen)")
    void deveAceitarCepComHifen() {
        Map<String, Object> body = new HashMap<>();
        body.put("logradouro", "Rua A");
        body.put("bairro", "B");
        body.put("localidade", "C");
        body.put("uf", "SP");

        when(restTemplate.getForObject(
            eq("http://viacep.com.br/ws/01001000/json/"), eq(Map.class)))
            .thenReturn(body);

        EnderecoInfo resultado = service.buscarPorCep("01001-000");

        assertThat(resultado.getLogradouro()).isEqualTo("Rua A");
    }

    @Test
    @DisplayName("RF-11 CA-03: Deve lançar CepInvalidoException para CEP curto")
    void deveLancarExcecaoParaCepCurto() {
        assertThatThrownBy(() -> service.buscarPorCep("1234"))
            .isInstanceOf(CepInvalidoException.class)
            .hasMessage("CEP deve ter 8 dígitos");
    }

    @Test
    @DisplayName("RF-11 CA-03: Deve lançar CepInvalidoException para CEP longo")
    void deveLancarExcecaoParaCepLongo() {
        assertThatThrownBy(() -> service.buscarPorCep("123456789"))
            .isInstanceOf(CepInvalidoException.class)
            .hasMessage("CEP deve ter 8 dígitos");
    }

    @Test
    @DisplayName("RF-10 CA-04: Deve lançar CepNaoEncontradoException quando body é null")
    void deveLancarExcecaoParaBodyNull() {
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
            .thenReturn(null);

        assertThatThrownBy(() -> service.buscarPorCep("99999999"))
            .isInstanceOf(CepNaoEncontradoException.class)
            .hasMessage("CEP não encontrado");
    }

    @Test
    @DisplayName("RF-10 CA-04: Deve lançar CepNaoEncontradoException quando body contém erro")
    void deveLancarExcecaoParaBodyComErro() {
        Map<String, Object> body = new HashMap<>();
        body.put("erro", true);

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
            .thenReturn(body);

        assertThatThrownBy(() -> service.buscarPorCep("99999999"))
            .isInstanceOf(CepNaoEncontradoException.class)
            .hasMessage("CEP não encontrado");
    }

    @Test
    @DisplayName("RF-10 CA-07: Deve lançar ApiIndisponivelException para erro de rede")
    void deveLancarExcecaoParaErroDeRede() {
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
            .thenThrow(new RestClientException("Connection refused"));

        assertThatThrownBy(() -> service.buscarPorCep("01001000"))
            .isInstanceOf(ApiIndisponivelException.class)
            .hasMessage("Serviço de CEP indisponível");
    }
}
