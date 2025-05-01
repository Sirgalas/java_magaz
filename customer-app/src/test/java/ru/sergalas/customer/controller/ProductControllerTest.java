package ru.sergalas.customer.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.sergalas.customer.client.FavoriteProductsClient;
import ru.sergalas.customer.client.ProductReviewsClient;
import ru.sergalas.customer.client.ProductsClient;
import ru.sergalas.customer.controler.ProductController;
import ru.sergalas.customer.entity.Product;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductsClient productsClient;

    @Mock
    FavoriteProductsClient favoriteProductsService;

    @Mock
    ProductReviewsClient productReviewsClient;

    @InjectMocks
    ProductController controller;

    @Test
    void loadProduct_ProductExists_Returns_NotEmptyMono() {
        //give
        var product =  new Product(1,"Тестовый товар","Описание тестового товара");
        doReturn(Mono.just(product)).when(this.productsClient).findProductById(product.id());
        //when
        StepVerifier.create(this.controller.getProduct(product.id()))
                //then
            .expectNext(new Product(1,"Тестовый товар","Описание тестового товара"))
            .expectComplete()
                .verify();

        verify(this.productsClient).findProductById(product.id());
        verifyNoMoreInteractions(this.productsClient);
        verifyNoInteractions(this.favoriteProductsService, this.productReviewsClient);
    }

    @Test
    void loadProduct_ProductDoesExists_ReturnsMonoNoSuchElementException() {
        //give
        doReturn(Mono.empty()).when(this.productsClient).findProductById(1);
        //when
        StepVerifier.create(this.controller.getProduct(1))
                //then
            .expectErrorMatches(exception -> exception instanceof NoSuchElementException e &&
                e.getMessage().equals("customer.products.error.not_found")
            )
                .verify();

        verify(this.productsClient).findProductById(1);
        verifyNoMoreInteractions(this.productsClient);
        verifyNoInteractions(this.favoriteProductsService, this.productReviewsClient);
    }


    @Test
    @DisplayName("handleNoSuchElementException display 404")
    public void handleNoSuchElementException_ReturnErrors404() {
        //given
        var exception = new NoSuchElementException("Товар не найден");
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();
        //when
        var result = this.controller.handleNoSuchElementException(exception, model,response);
        //then
        assertEquals("errors/404",result);
        assertEquals("Товар не найден",  model.getAttribute("error"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
