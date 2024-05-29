package ru.sergalas.magaz.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClient;
import ru.sergalas.magaz.web.clients.ProductsRestClientImpl;

@Configuration
public class ClientsBeans {

    @Bean
    public ProductsRestClientImpl productsRestClient(
            @Value("${magaz.service.catalogue.uri:http://localhost:8081}") String catalogueBaseUri,
            @Value("${magaz.service.catalogue.username:}") String catalogue_username,
            @Value("${magaz.service.catalogue.password:}") String catalogue_password
    ) {
        return new ProductsRestClientImpl(RestClient.builder()
            .baseUrl(catalogueBaseUri)
                .requestInterceptor(new BasicAuthenticationInterceptor(catalogue_username,catalogue_password))
            .build());
    }
}
