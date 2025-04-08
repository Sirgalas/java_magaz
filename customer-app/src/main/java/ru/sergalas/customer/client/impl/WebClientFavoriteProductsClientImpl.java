package ru.sergalas.customer.client.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.client.FavoriteProductsClient;
import ru.sergalas.customer.client.exeception.ClientBadRequestException;
import ru.sergalas.customer.client.payload.NewFavoriteProductPayload;
import ru.sergalas.customer.entity.FavoriteProduct;

import java.util.List;

@RequiredArgsConstructor
public class WebClientFavoriteProductsClientImpl implements FavoriteProductsClient {

    private final WebClient webClient;


    @Override
    public Mono<FavoriteProduct> findFavoriteProductByProductId(Integer productId) {
        return this.webClient.get().uri("/feedback-api/favorite-products/by-product-id/{productId}", productId)
            .retrieve()
            .bodyToMono(FavoriteProduct.class)
            .onErrorComplete(WebClientResponseException.NotFound.class);
    }

    @Override
    public Mono<FavoriteProduct> addProductToFavorites(Integer id) {
        return this.webClient.post()
            .uri("/feedback-api/favorite-products/by-product-id")
            .bodyValue(new NewFavoriteProductPayload(id))
            .retrieve()
            .bodyToMono(FavoriteProduct.class)
            .onErrorMap(
                WebClientResponseException.BadRequest.class,
                ex -> new ClientBadRequestException(
                    ex,
                    ((List<String>) ex.getResponseBodyAs(ProblemDetail.class).getProperties().get("errors"))
                )
            );
    }

    @Override
    public Mono<Void> removeProductFromFavorites(Integer id) {
        return this.webClient.delete()
            .uri("feedback-api/product-reviews/by-product-id/{productId}",id)
            .retrieve()
            .toBodilessEntity()
            .then();
    }

    @Override
    public Flux<FavoriteProduct> findFavoriteProducts() {
        return this.webClient.get().uri("/feedback-api/favorite-products")
            .retrieve()
            .bodyToFlux(FavoriteProduct.class);
    }
}
