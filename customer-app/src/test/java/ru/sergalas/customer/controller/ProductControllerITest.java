package ru.sergalas.customer.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@SpringBootTest
@AutoConfigureWebTestClient
@WireMockTest(
     httpPort = 56432
)
class ProductControllerITest {
    @Autowired
    WebTestClient webClient;


    /*@BeforeEach
    void setUp() {
        stubFor(get("/catalogue-api/products/1")
                .willReturn(okJson("""
                        {
                            "id": 1,
                            "title": "Название товара №1",
                            "details": "Описание товара №1"
                        }""")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
    }*/

    @Test
    void addToFavourites_RequestValid_ReturnsRedirectToProductPage() {
        //given
        WireMock.
            stubFor(
                WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.okJson(
                """
                {
                      "id":1,
                      "title": "Название товара",
                      "details": "Описание товара"
                }
                """)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            )
        );

        WireMock.stubFor(WireMock.post("/feedback-api/favorite-products/by-product-id")
            .withRequestBody(WireMock.equalToJson("""
          {
            "productId" : 1
          }       
        """)).withHeader(HttpHeaders.CONTENT_TYPE, equalTo( MediaType.APPLICATION_JSON_VALUE))
        );

        //when
        this.webClient.mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/product/1/add-to-favourites")
        //then
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/customer/product/1");

        verify(getRequestedFor(urlPathMatching("/catalogue-api/products/1")));
        verify(
            postRequestedFor(
                urlPathMatching("/feedback-api/favorite-products/by-product-id")
            )
            .withRequestBody(equalToJson("""
            {
              "productId" : 1
            }
            """))
        );
    }@Test
    void addToFavourites_Product5DoesNotExist_ReturnsRedirectNotFoundPage() {
        //given

        //when
        this.webClient.mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/product/1/add-to-favourites")
                //then
                .exchange()
                .expectStatus().isNotFound();

        verify(getRequestedFor(urlPathMatching("/catalogue-api/products/1")));
    }

}