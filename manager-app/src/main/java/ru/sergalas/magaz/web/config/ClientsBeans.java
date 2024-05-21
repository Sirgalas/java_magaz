package ru.sergalas.magaz.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.sergalas.magaz.web.clients.ProductsRestClientImpl;

@Configuration
public class ClientsBeans {

    @Bean
    public ProductsRestClientImpl productsRestClient(
            @Value("${magaz.service.catalogue.uri:http://localhost:8081}") String catalogueBaseUri
    ) {
        return new ProductsRestClientImpl(RestClient.builder()
            .baseUrl(catalogueBaseUri)
            .build());
    }
}
