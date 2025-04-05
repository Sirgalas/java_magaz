package ru.sergalas.feedback.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.sergalas.feedback.entity.ProductReview;
import ru.sergalas.feedback.payload.NewProductReviewPayload;
import ru.sergalas.feedback.service.ProductReviewsService;

import java.util.Map;

@RestController
@RequestMapping("/feedback-api/product-reviews")
@RequiredArgsConstructor
public class ProductReviewsRestController {
    private final ProductReviewsService service;

    @GetMapping("by-product-id/{productId:\\d+}")
    public Flux<ProductReview> findProductReviewsByProductId(@PathVariable Integer productId) {
        return this.service.findProductReviewsByProductId(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
        @Valid @RequestBody Mono<NewProductReviewPayload> payloadRequest,
        UriComponentsBuilder uriComponentsBuilder
    ) {
        return payloadRequest
            .flatMap(payload -> this.service.createProductReview(
                payload.productId(),
                payload.rating(),
                payload.reviews()
            ).map(productReview -> ResponseEntity
                .created(
                    uriComponentsBuilder
                        .replacePath("/feedback-api/product-reviews/{id}")
                            .build(
                                Map.of("id",productReview.getId())
                            )

                ).
                body(productReview)
            )
        );
    }
}
