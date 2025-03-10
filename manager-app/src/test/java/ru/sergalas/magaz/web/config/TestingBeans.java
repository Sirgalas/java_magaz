package ru.sergalas.magaz.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;
import ru.sergalas.magaz.web.clients.ProductsRestClient;
import ru.sergalas.magaz.web.clients.ProductsRestClientImpl;

import static org.mockito.Mockito.mock;

@Configuration
public class TestingBeans {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return mock(ClientRegistrationRepository.class);
    }

    @Bean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository() {
       return mock(OAuth2AuthorizedClientRepository.class);
    }

    @Bean
    @Primary
    public ProductsRestClientImpl testProductsRestClient (@Value("${magaz.service.catalogue.uri:http://localhost:54321}") String baseUrl){
        return new ProductsRestClientImpl(RestClient.builder().baseUrl(baseUrl).build());
    }
}
