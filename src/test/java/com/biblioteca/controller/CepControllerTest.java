package com.biblioteca.controller;

import com.biblioteca.dto.EnderecoInfo;
import com.biblioteca.exception.ApiIndisponivelException;
import com.biblioteca.exception.CepInvalidoException;
import com.biblioteca.exception.CepNaoEncontradoException;
import com.biblioteca.service.CepLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Testes do CepController — REST.
 * Conforme RF-10.
 */
@ExtendWith(MockitoExtension.class)
class CepControllerTest {

    @Mock
    private CepLookupService cepLookupService;

    @InjectMocks
    private CepController cepController;

    @Test
    @DisplayName("RF-10: Deve retornar 200 com endereço para CEP válido")
    void deveRetornar200ParaCepValido() {
        EnderecoInfo endereco = new EnderecoInfo("Rua A", "Bairro B", "Cidade C", "SP");
        when(cepLookupService.buscarPorCep("01001000")).thenReturn(endereco);

        ResponseEntity<?> response = cepController.buscarCep("01001000");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(EnderecoInfo.class);
    }

    @Test
    @DisplayName("RF-11 CA-03: Deve retornar 400 para CEP inválido")
    void deveRetornar400ParaCepInvalido() {
        when(cepLookupService.buscarPorCep("123")).thenThrow(new CepInvalidoException("CEP inválido"));

        ResponseEntity<?> response = cepController.buscarCep("123");

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    @DisplayName("RF-10 CA-04: Deve retornar 404 para CEP não encontrado")
    void deveRetornar404ParaCepNaoEncontrado() {
        when(cepLookupService.buscarPorCep("99999999")).thenThrow(new CepNaoEncontradoException("Não encontrado"));

        ResponseEntity<?> response = cepController.buscarCep("99999999");

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    @DisplayName("RF-10 CA-07: Deve retornar 503 para API indisponível")
    void deveRetornar503ParaApiIndisponivel() {
        when(cepLookupService.buscarPorCep("01001000")).thenThrow(new ApiIndisponivelException("Indisponível"));

        ResponseEntity<?> response = cepController.buscarCep("01001000");

        assertThat(response.getStatusCode().value()).isEqualTo(503);
    }
}
