package ru.sergalas.feedback.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.feedback.entity.FavoriteProduct;
import ru.sergalas.feedback.payload.NewFavoriteProductPayload;
import ru.sergalas.feedback.service.FavoriteProductsService;

@RestController
@RequestMapping("feedback-api/favorite-products")
@RequiredArgsConstructor
public class FavoriteProductsRestController {

    private final FavoriteProductsService favoriteProductsService;

    @GetMapping
    public Flux<FavoriteProduct> findFavoriteProducts() {
        return favoriteProductsService.findFavoriteProducts();
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavoriteProduct> findFavoriteProductByProductId(@PathVariable("productId") Integer productId) {
        return favoriteProductsService.findFavoriteProductByProduct(productId);
    }

    @PostMapping("by-product-id")
    public Mono<ResponseEntity<FavoriteProduct>> addFavoriteProduct(
            @Valid @RequestBody Mono<NewFavoriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
        ) {
        return payloadMono.
            flatMap(payload -> favoriteProductsService
                .addProductToFavorites(payload.productId())
                .map(favoriteProduct -> ResponseEntity
                    .created(
                        uriComponentsBuilder.replacePath("feedback-api/favorite-products/{id}")
                        .build(favoriteProduct.getId()))
                    .body(favoriteProduct)
                )
        );
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeFavoriteProduct(@PathVariable("productId") Integer productId) {
        return this.favoriteProductsService.removeProductFromFavorites(productId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
