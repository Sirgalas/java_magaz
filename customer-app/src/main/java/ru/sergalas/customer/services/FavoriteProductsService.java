package ru.sergalas.customer.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.entity.FavoriteProduct;

public interface FavoriteProductsService {

    public Mono<FavoriteProduct> addProductToFavorites(Integer productId);

    Mono<Void> removeProductFromFavorites(Integer productId);

    Mono<FavoriteProduct> findFavoriteProductByProduct(Integer productId);

    Flux<FavoriteProduct> findFavoriteProducts();
}
