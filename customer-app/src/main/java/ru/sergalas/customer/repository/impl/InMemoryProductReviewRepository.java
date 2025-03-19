package ru.sergalas.customer.repository.impl;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.entity.ProductReview;
import ru.sergalas.customer.repository.ProductReviewRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Repository
public class InMemoryProductReviewRepository implements ProductReviewRepository {
    private final List<ProductReview> productReviews = Collections.synchronizedList(new LinkedList<>());

    @Override
    public Mono<ProductReview> save(ProductReview productReview) {
        productReviews.add(productReview);
        return Mono.just(productReview);
    }

    @Override
    public Flux<ProductReview> findAllByProductId(Integer productId) {
        return  Flux.fromIterable(productReviews)
                .filter(productReview -> productReview.getProductId().equals(productId));
    }
}
