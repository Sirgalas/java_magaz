package ru.sergalas.customer.client.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import ru.sergalas.customer.client.ProductsClient;
import ru.sergalas.customer.entity.Product;

@RequiredArgsConstructor
public class WebClientProductsClientImpl implements ProductsClient {

    private final WebClient webClient;

    @Override
    public Flux<Product> findProducts(String filter) {
        return this.webClient.get()
                .uri("http://localhost:8081/catalogue-api/products?filter={}", filter)
                .retrieve()
                .bodyToFlux(Product.class);
    }
}
