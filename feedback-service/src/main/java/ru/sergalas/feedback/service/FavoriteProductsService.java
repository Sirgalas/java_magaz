package ru.sergalas.feedback.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.feedback.entity.FavoriteProduct;

public interface FavoriteProductsService {

    public Mono<FavoriteProduct> addProductToFavorites(Integer productId, String userId);

    Mono<Void> removeProductFromFavorites(Integer productId, String userId);

    Mono<FavoriteProduct> findFavoriteProductByProduct(Integer productId, String UserId);

    Flux<FavoriteProduct> findFavoriteProducts(String userId);
}
