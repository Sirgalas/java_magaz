package ru.sergalas.customer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.sergalas.customer.client.impl.WebClientFavoriteProductsClientImpl;
import ru.sergalas.customer.client.impl.WebClientProductReviewsClient;
import ru.sergalas.customer.client.impl.WebClientProductsClient;

@Configuration
public class ClientConfig {

    @Bean
    public WebClientProductsClient webClientConfiguration(
            @Value("${selmag.services.catalogue.uri:http://localhost:8081}") String uri
    ) {
        var webClient = WebClient
                .builder()
                .baseUrl(uri)
                .filter((request, next) -> {
                    System.out.println("Sending request to: " + request.url()); // Логирование URL
                    return next.exchange(request);
                });;
        return new WebClientProductsClient(webClient.build());
    }

    @Bean
    public WebClientFavoriteProductsClientImpl webClientFavoriteProductsClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8083}") String uri
    ) {
        return new WebClientFavoriteProductsClientImpl(WebClient.builder()
                .baseUrl(uri)
                .build());
    }

    @Bean
    public WebClientProductReviewsClient webClientProductReviewsClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8083}") String uri
    ) {
        return new WebClientProductReviewsClient(WebClient.builder().baseUrl(uri).build());
    }
}
