package ru.sergalas.customer.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.entity.ProductReview;
import ru.sergalas.customer.payload.ProductReviewPayload;

public interface ProductReviewsClient {
    Flux<ProductReview> findProductReviewsByProductId(Integer productId);

    Mono<ProductReview> createProductReview(int productId, ProductReviewPayload productReviewPayload);
}
