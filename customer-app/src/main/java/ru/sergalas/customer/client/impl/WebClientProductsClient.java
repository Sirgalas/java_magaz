package ru.sergalas.customer.client.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.client.ProductsClient;
import ru.sergalas.customer.client.exeception.ClientBadRequestException;
import ru.sergalas.customer.entity.Product;

import java.util.List;

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
                .bodyToMono(Product.class)
                .onErrorComplete(WebClientResponseException.NotFound.class)
                .onErrorMap(
                    WebClientResponseException.BadRequest.class,
                    ex -> new ClientBadRequestException(
                        ex,
                        ((List<String>) ex.getResponseBodyAs(ProblemDetail.class).getProperties().get("errors"))
                    )
                );
    }
}
