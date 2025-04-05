package ru.sergalas.customer.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.entity.FavoriteProduct;

import java.util.Optional;

public interface FavoriteProductsClient {
    Mono<FavoriteProduct> findFavoriteProductByProductId(Integer productId);

    Mono<FavoriteProduct> addProductToFavorites(Integer id);

    Mono<Void> removeProductFromFavorites(Integer id);

    Flux<FavoriteProduct> findFavoriteProducts();
}
