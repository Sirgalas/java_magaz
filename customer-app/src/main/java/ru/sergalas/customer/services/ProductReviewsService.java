package ru.sergalas.customer.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.entity.ProductReview;

public interface ProductReviewsService {

    Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review);

    Flux<ProductReview> findProductReviewsByProductId(Integer productId);
}
