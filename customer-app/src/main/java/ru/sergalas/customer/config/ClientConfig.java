package ru.sergalas.customer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.sergalas.customer.client.impl.WebClientProductsClientImpl;

@Configuration
public class ClientConfig {

    @Bean
    public WebClientProductsClientImpl webClientConfiguration(
            @Value("${selmag.services.catalogue.uri:http://localhost:8081/}") String uri
    ) {
        var webClient = WebClient
                .builder()
                .baseUrl(uri);
        return new WebClientProductsClientImpl(webClient.build());
    }
}
