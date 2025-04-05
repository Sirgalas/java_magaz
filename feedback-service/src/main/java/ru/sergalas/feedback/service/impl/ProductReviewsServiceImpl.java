    package ru.sergalas.feedback.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.feedback.entity.ProductReview;
import ru.sergalas.feedback.repository.ProductReviewRepository;
import ru.sergalas.feedback.service.ProductReviewsService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductReviewsServiceImpl implements ProductReviewsService {

    private final ProductReviewRepository productReviewRepository;

    @Override
    public Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review) {
        return this.productReviewRepository.save(new ProductReview(UUID.randomUUID(), productId, rating, review));
    }

    @Override
    public Flux<ProductReview> findProductReviewsByProductId(Integer productId) {
        return this.productReviewRepository.findAllByProductId(productId);
    }
}
