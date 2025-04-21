package ru.sergalas.feedback.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
    public Flux<FavoriteProduct> findFavoriteProducts(Mono<JwtAuthenticationToken> jwtAuthenticationToken,
                                                      Authentication authentication) {
        return jwtAuthenticationToken.flatMapMany(
                token -> favoriteProductsService.findFavoriteProducts(token.getToken().getSubject())
        );
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavoriteProduct> findFavoriteProductByProductId(
            Mono<JwtAuthenticationToken> jwtAuthenticationToken,
            @PathVariable("productId") Integer productId) {
        return jwtAuthenticationToken.flatMap(
                token -> favoriteProductsService.findFavoriteProductByProduct(productId,token.getToken().getSubject())
        );
    }

    @PostMapping("by-product-id")
    public Mono<ResponseEntity<FavoriteProduct>> addFavoriteProduct(
            Mono<JwtAuthenticationToken> jwtAuthenticationToken,
            @Valid @RequestBody Mono<NewFavoriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
        ) {
        return Mono.zip(jwtAuthenticationToken, payloadMono).
            flatMap(tuple -> favoriteProductsService
                .addProductToFavorites(tuple.getT2().productId(),tuple.getT1().getToken().getSubject())
                .map(favoriteProduct -> ResponseEntity
                    .created(
                        uriComponentsBuilder.replacePath("feedback-api/favorite-products/{id}")
                        .build(favoriteProduct.getId()))
                    .body(favoriteProduct)
                )
            );
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeFavoriteProduct(
            Mono<JwtAuthenticationToken> jwtAuthenticationToken,
            @PathVariable("productId") Integer productId) {
        return jwtAuthenticationToken.flatMap(token -> this.favoriteProductsService
            .removeProductFromFavorites(productId,token.getToken().getSubject())
        ).then(Mono.just(ResponseEntity.noContent().build()));
    }
}
