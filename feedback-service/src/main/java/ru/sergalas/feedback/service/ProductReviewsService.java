package ru.sergalas.feedback.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.feedback.entity.ProductReview;

public interface ProductReviewsService {

    Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review);

    Flux<ProductReview> findProductReviewsByProductId(Integer productId);
}
