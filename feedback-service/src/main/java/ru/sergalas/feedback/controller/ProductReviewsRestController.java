package ru.sergalas.feedback.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
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
@Slf4j
public class ProductReviewsRestController {
    private final ProductReviewsService service;
    private final ReactiveMongoTemplate mongoTemplate;

    @GetMapping("by-product-id/{productId:\\d+}")
    @Operation(
            security = @SecurityRequirement(name = "keycloak"),
            parameters = {
            @Parameter(name = "productId", description = "ID товара", example = "123", required = true)}
    )
    public Flux<ProductReview> findProductReviewsByProductId(
            @PathVariable Integer productId,
            @Parameter(hidden = true) Mono<JwtAuthenticationToken> principalMono
    ) {
        return principalMono.flatMapMany(principal -> this.mongoTemplate.find(Query.query(Criteria.where("productId").is(productId)), ProductReview.class));

    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
        @Valid @RequestBody Mono<NewProductReviewPayload> payloadRequest,
        UriComponentsBuilder uriComponentsBuilder
    ) {
        return authenticationTokenMono.flatMap(token -> payloadRequest
                .flatMap(payload -> this.service.createProductReview(
                    payload.productId(),
                    payload.rating(),
                    payload.review(),
                    token.getToken().getSubject()
                )
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
