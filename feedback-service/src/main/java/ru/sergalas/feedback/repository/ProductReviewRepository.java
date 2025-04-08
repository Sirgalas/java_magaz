package ru.sergalas.feedback.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.feedback.entity.ProductReview;

import java.util.UUID;


public interface ProductReviewRepository extends ReactiveCrudRepository<ProductReview, UUID> {

    @Query("{'productId': ?0}")
    Flux<ProductReview> findAllByProductId(Integer productId);
}
