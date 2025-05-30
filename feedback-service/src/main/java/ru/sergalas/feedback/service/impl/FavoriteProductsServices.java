package ru.sergalas.feedback.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.feedback.entity.FavoriteProduct;
import ru.sergalas.feedback.repository.FavoriteProductRepository;
import ru.sergalas.feedback.service.FavoriteProductsService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteProductsServices implements FavoriteProductsService {

    private final FavoriteProductRepository favoriteProductRepository;

    @Override
    public Mono<FavoriteProduct> addProductToFavorites(Integer productId,String userId) {
        return this.favoriteProductRepository.save(new FavoriteProduct(UUID.randomUUID(), productId, userId));
    }

    @Override
    public Mono<Void> removeProductFromFavorites(Integer productId,String userId) {
        return this.favoriteProductRepository.deleteByProductIdAndUserId(productId,userId);
    }

    @Override
    public Mono<FavoriteProduct> findFavoriteProductByProduct(Integer productId,String userId) {
        return this.favoriteProductRepository.findByProductIdAndUserId(productId,userId);
    }

    @Override
    public Flux<FavoriteProduct> findFavoriteProducts(String userId) {
        return this.favoriteProductRepository.findAllByUserId(userId);
    }
}
