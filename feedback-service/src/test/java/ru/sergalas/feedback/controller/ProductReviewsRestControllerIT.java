package ru.sergalas.feedback.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import ru.sergalas.feedback.entity.ProductReview;
import ru.sergalas.feedback.repository.ProductReviewRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
class ProductReviewsRestControllerIT {
    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    WebTestClient webClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        this.reactiveMongoTemplate.insertAll(
                List.of(
                        new ProductReview(UUID.fromString("74763b03-1737-46c2-8cdd-3887512ccaf6"),1,1,"Отзыв 1", "user-1"),
                        new ProductReview(UUID.fromString("6a4d4aa6-bb6e-4800-9f22-ca0694941cdf"),1,2,"Отзыв 2", "user-2"),
                        new ProductReview(UUID.fromString("4c5a998f-9041-4275-98e8-37d4e44d5c4d"),1,3,"Отзыв 3", "user-3")
                )
        ).blockLast();
    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(ProductReview.class).all().block();
    }

    @Test
    void findProductReviewsByProductId_ReturnsReviews() {
        // when


        //then
        this.webClient.mutateWith(mockJwt())
                .mutate()
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                    log.info("<== REQUEST ==>");
                    log.info("{} {}", clientRequest.method(), clientRequest.url());
                    clientRequest.headers().forEach((headerName, headers) -> log.info("{}: {}", headerName, headers));
                    log.info("<== END ==>");
                    return Mono.just(clientRequest);
                }))
                .build()
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(response -> {
                    log.info("Actual JSON: {}", new String(response.getResponseBody()));
                })
                .json("""
                        [
                            {
                              "id": "74763b03-1737-46c2-8cdd-3887512ccaf6",
                              "productId": 1,
                              "rating": 1,
                              "review": "Отзыв 1",
                              "userId": "user-1"
                            },
                            {
                              "id": "6a4d4aa6-bb6e-4800-9f22-ca0694941cdf",
                              "productId": 1,
                              "rating": 2,
                              "review": "Отзыв 2",
                              "userId": "user-2"
                            },
                            {
                              "id": "4c5a998f-9041-4275-98e8-37d4e44d5c4d",
                              "productId": 1,
                              "rating": 3,
                              "review": "Отзыв 3",
                              "userId": "user-3"
                            }
                        ]
                        """);

    }

    @Test
    void findProductReviewsByProductId_UserIsNotAuth_ReturnsNotAuthorized() {
        this.webClient
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectStatus().isUnauthorized();

    }


    @Test
    void createProductReview_RequestValid_ReturnsCreatedProductReview() {
        //give

        //when
        webClient
            .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
            .post()
                .uri("/feedback-api/product-reviews")
            .contentType(MediaType.APPLICATION_JSON).bodyValue("""
              {
                  "productId": 1,
                  "rating": 3,
                  "review": "Отзыв 3"
              }
        """)
        //then
        .exchange()
            .expectStatus()
            .isCreated()
            .expectHeader().exists(HttpHeaders.LOCATION)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody().json("""
                {
                  "productId": 1,
                  "rating": 3,
                  "review": "Отзыв 3",
                  "userId": "user-tester"
                 }
             """).jsonPath("$.id")
            .exists();

    }


    @Test
    void createProductReview_RequestNotValid_ReturnsError() {
        //give

        //when
        webClient
            .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
            .post()
                .uri("/feedback-api/product-reviews")
            .contentType(MediaType.APPLICATION_JSON).bodyValue("""
              {
                  "productId": null,
                  "rating": -1,
                  "review": "Отзыв 3"
              }
        """)
        //then
        .exchange()
            .expectStatus()
            .isBadRequest()
            .expectHeader().doesNotExist(HttpHeaders.LOCATION)
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody().json("""
                {
                  "errors": [
                       "{customer.products.reviews.create.errors.product_id_is_null}",
                       "{customer.products.reviews.create.errors.rating_is_bellow_min}"
                   ]
                }
             """);

    }

}