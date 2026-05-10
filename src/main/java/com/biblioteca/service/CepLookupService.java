package com.biblioteca.service;

import com.biblioteca.dto.EnderecoInfo;
import com.biblioteca.exception.ApiIndisponivelException;
import com.biblioteca.exception.CepInvalidoException;
import com.biblioteca.exception.CepNaoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Serviço de consulta de CEP via API ViaCEP.
 * Conforme RF-10 seção 9.
 */
@Service
public class CepLookupService {

    private static final Logger log = LoggerFactory.getLogger(CepLookupService.class);

    private final RestTemplate restTemplate;
    private final String viacepUrl;

    public CepLookupService(RestTemplate restTemplate,
                            @Value("${viacep.api.url}") String viacepUrl) {
        this.restTemplate = restTemplate;
        this.viacepUrl = viacepUrl;
    }

    /**
     * Busca endereço por CEP na API ViaCEP.
     * Conforme RF-10 seção 9.
     *
     * @param cep CEP com ou sem formatação
     * @return EnderecoInfo com logradouro, bairro, localidade, uf
     * @throws CepInvalidoException se o formato do CEP for inválido
     * @throws CepNaoEncontradoException se o CEP não for encontrado
     * @throws ApiIndisponivelException se a API estiver indisponível
     */
    @SuppressWarnings("unchecked")
    public EnderecoInfo buscarPorCep(String cep) {
        String cepLimpo = cep.replaceAll("[^0-9]", "");

        if (cepLimpo.length() != 8) {
            throw new CepInvalidoException("CEP deve ter 8 dígitos");
        }

        try {
            log.info("Consultando CEP {} na API ViaCEP", cepLimpo);
            String url = viacepUrl + cepLimpo + "/json/";

            Map<String, Object> body = restTemplate.getForObject(url, Map.class);

            if (body == null || body.containsKey("erro")) {
                throw new CepNaoEncontradoException("CEP não encontrado");
            }

            return new EnderecoInfo(
                (String) body.get("logradouro"),
                (String) body.get("bairro"),
                (String) body.get("localidade"),
                (String) body.get("uf")
            );
        } catch (RestClientException e) {
            log.error("Erro ao consultar API ViaCEP: {}", e.getMessage());
            throw new ApiIndisponivelException("Serviço de CEP indisponível");
        }
    }
}
