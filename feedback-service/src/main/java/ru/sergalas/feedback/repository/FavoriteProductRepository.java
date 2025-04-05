package ru.sergalas.feedback.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.feedback.entity.FavoriteProduct;

public interface FavoriteProductRepository {
    Mono<FavoriteProduct> save(FavoriteProduct favoriteProduct);

    Mono<Void> deleteByProductId(Integer productId);

    Mono<FavoriteProduct> findByProductId(Integer productId);

    Flux<FavoriteProduct> findAll();
}
