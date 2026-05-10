package com.biblioteca.controller;

import com.biblioteca.dto.EnderecoInfo;
import com.biblioteca.exception.ApiIndisponivelException;
import com.biblioteca.exception.CepInvalidoException;
import com.biblioteca.exception.CepNaoEncontradoException;
import com.biblioteca.service.CepLookupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller REST para consulta de CEP via API ViaCEP.
 * Endpoint público (não requer autenticação — SecurityConfig).
 * Conforme RF-10 seção 8.
 */
@RestController
@RequestMapping("/api/cep")
public class CepController {

    private final CepLookupService cepLookupService;

    public CepController(CepLookupService cepLookupService) {
        this.cepLookupService = cepLookupService;
    }

    /**
     * Busca endereço por CEP.
     * Retorna JSON com logradouro, bairro, localidade, uf.
     *
     * @return 200 com EnderecoInfo, 400 se formato inválido, 404 se não encontrado, 503 se API indisponível
     */
    @GetMapping("/{cep}")
    public ResponseEntity<?> buscarCep(@PathVariable String cep) {
        try {
            EnderecoInfo endereco = cepLookupService.buscarPorCep(cep);
            return ResponseEntity.ok(endereco);
        } catch (CepInvalidoException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("mensagem", e.getMessage()));
        } catch (CepNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensagem", e.getMessage()));
        } catch (ApiIndisponivelException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("mensagem", e.getMessage()));
        }
    }
}
