package ru.sergalas.feedback.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.feedback.entity.FavoriteProduct;

import java.util.UUID;

public interface FavoriteProductRepository extends ReactiveCrudRepository<FavoriteProduct, UUID> {

    Flux<FavoriteProduct> findAllByUserId(String userId);

    Mono<Void> deleteByProductIdAndUserId(Integer productId, String userId);

    Mono<FavoriteProduct> findByProductIdAndUserId(Integer productId, String userId);

}
