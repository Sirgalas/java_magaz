package ru.sergalas.feedback.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.feedback.entity.FavoriteProduct;

public interface FavoriteProductsService {

    public Mono<FavoriteProduct> addProductToFavorites(Integer productId);

    Mono<Void> removeProductFromFavorites(Integer productId);

    Mono<FavoriteProduct> findFavoriteProductByProduct(Integer productId);

    Flux<FavoriteProduct> findFavoriteProducts();
}
