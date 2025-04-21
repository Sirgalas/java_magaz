package ru.sergalas.customer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import ru.sergalas.customer.client.impl.WebClientFavoriteProductsClientImpl;
import ru.sergalas.customer.client.impl.WebClientProductReviewsClient;
import ru.sergalas.customer.client.impl.WebClientProductsClient;

@Configuration
public class ClientConfig {

    @Bean
    @Scope("prototype")
    public WebClient.Builder selmagWebClientBuilder(
        ReactiveClientRegistrationRepository clientRegistrationRepository,
        ServerOAuth2AuthorizedClientRepository auth2AuthorizedClientRepository
    ) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction filter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, auth2AuthorizedClientRepository);
        filter.setDefaultClientRegistrationId("keycloak");
        return WebClient
            .builder()
            .filter(filter);
    }

    @Bean
    public WebClientProductsClient webClientConfiguration(
            @Value("${selmag.services.catalogue.uri:http://localhost:8081}") String uri,
            WebClient.Builder selmagWebClientBuilder
    ) {
        var selmagClient = selmagWebClientBuilder
            .baseUrl(uri)
            .filter((request, next) -> {
                System.out.println("Sending request to: " + request.url()); // Логирование URL
                return next.exchange(request);
            });;
        return new WebClientProductsClient(selmagClient.build());
    }

    @Bean
    public WebClientFavoriteProductsClientImpl webClientFavoriteProductsClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8083}") String uri,
            WebClient.Builder selmagWebClientBuilder
    ) {
        return new WebClientFavoriteProductsClientImpl(
            selmagWebClientBuilder
                .baseUrl(uri)
                .build());
    }

    @Bean
    public WebClientProductReviewsClient webClientProductReviewsClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8083}") String uri,
            WebClient.Builder selmagWebClientBuilder
    ) {
        return new WebClientProductReviewsClient(
            selmagWebClientBuilder
                .baseUrl(uri)
                .build()
        );
    }
}
