package ru.sergalas.customer.client.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.client.ProductReviewsClient;
import ru.sergalas.customer.client.exeception.ClientBadRequestException;
import ru.sergalas.customer.entity.ProductReview;
import ru.sergalas.customer.client.payload.NewProductReviewsPayload;
import ru.sergalas.customer.payload.ProductReviewPayload;

import java.util.List;

@RequiredArgsConstructor
public class WebClientProductReviewsClient implements ProductReviewsClient {

    private static final Logger log = LoggerFactory.getLogger(WebClientProductReviewsClient.class);
    private final WebClient webClient;

    @Override
    public Flux<ProductReview> findProductReviewsByProductId(Integer productId) {
        return webClient
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/{productId}", productId)
                .retrieve()
                .bodyToFlux(ProductReview.class);
    }

    @Override
    public Mono<ProductReview> createProductReview(Integer productId, ProductReviewPayload productReviewPayload) {
        return webClient
                .post()
                .uri("feedback-api/product-reviews")
                .bodyValue (
                    new NewProductReviewsPayload(
                        productId,
                        productReviewPayload.rating(),
                        productReviewPayload.review()
                    )
                )
                .retrieve()
                .bodyToMono(ProductReview.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        exception -> new ClientBadRequestException(exception,
                                ((List<String>) exception.getResponseBodyAs(ProblemDetail.class)
                                        .getProperties().get("errors"))));
    }
}
