package com.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração do RestTemplate para chamadas HTTP externas.
 * Timeouts conforme RNF-06 seção 4.2.
 */
@Configuration
public class WebClientConfig {

    /**
     * RestTemplate com timeouts configurados para a API ViaCEP.
     * ConnectTimeout: 3 segundos.
     * ReadTimeout: 5 segundos (RF-10 RN-04).
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }
}
