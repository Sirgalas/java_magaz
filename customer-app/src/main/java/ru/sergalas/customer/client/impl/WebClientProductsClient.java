package ru.sergalas.customer.client.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.client.ProductsClient;
import ru.sergalas.customer.entity.Product;

@RequiredArgsConstructor
public class WebClientProductsClient implements ProductsClient {

    private final WebClient webClient;

    @Override
    public Flux<Product> findProducts(String filter) {
        return this.webClient.get()
                .uri("/catalogue-api/products?filter={filter}", filter)
                .retrieve()
                .bodyToFlux(Product.class);
    }

    @Override
    public Mono<Product> findProductById(int productId) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/catalogue-api/products/{productId}")
                        .build(productId))
                .retrieve()
                .bodyToMono(Product.class);
    }
}
