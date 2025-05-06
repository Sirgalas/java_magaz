package ru.sergalas.magaz.rest.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import ru.sergalas.magaz.rest.controller.payloads.EditProductPayload;
import ru.sergalas.magaz.rest.entity.Product;
import ru.sergalas.magaz.rest.services.ProductService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRestControllerMTest {

    @Mock
    ProductService productService;
    @Mock
    MessageSource messageSource;
    @InjectMocks
    ProductRestController productRestController;

    @Test
    void getProduct_ProductExist_ReturnsProduct() {
        //given
        var product = new Product(1, "Название продукта", "Описание продукта");
        doReturn(Optional.of(product)).when(productService).findProduct(1);
        //when
        var result = productRestController.getProduct(1);
        //then
        assertEquals(product, result);
    }

    @Test
    void getProduct_ProductNotExist_ThrowsNoSuchElementException() {
        var exception = assertThrows(NoSuchElementException.class, () -> {this.productRestController.getProduct(1);});
        assertEquals("catalogue.errors.product.not_found", exception.getMessage());
    }

    @Test
    void findProduct_ReturnsProduct() {
        var product = new Product(1, "Название товара", "Описание товара");
        var result = productRestController.findProduct(product);
        assertEquals(product,result);
    }

    @Test
    void updateProduct_RequestIsValid_ReturnNOContent() throws BindException {
        var payload = new EditProductPayload("Новое название","Новое название");
        var bindingResult = new MapBindingResult(Map.of(),"payload");
        var result = productRestController.updateProduct(1, payload,bindingResult);
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(productService).updateProduct(1,"Новое название","Новое название");
    }

    @Test
    void updateProduct_RequestIsInvalid_ReturnsBadRequest() throws BindException {
        var payload =  new EditProductPayload(" ",null);
        var bindingResult = new MapBindingResult(Map.of(),"payload");
        bindingResult.addError(new FieldError("payload","title","error"));

        var exception = assertThrows(
            BindException.class,
            () -> {productRestController.updateProduct(1,payload,bindingResult);}
        );

        assertEquals(List.of(new FieldError("payload","title","error")),exception.getAllErrors());
    }

    @Test
    void deleteProduct_RequestIsValid_ReturnNoContent() {
        var result = productRestController.deleteProduct(1);
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(this.productService).deleteProduct(1);
    }

    @Test
    void handleNoSuchElementException_ReturnNotFound() {
        var exception = new NoSuchElementException("error_code");
        var locale = Locale.of("ru");

        doReturn("error_details").when(messageSource)
                .getMessage("error_code", new Object[0],"error_code", Locale.of("ru"));

        var result = productRestController.handleNoSuchElementException(exception,locale);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), (result.getBody()).getStatus());
        assertEquals("error_details", result.getBody().getDetail());

        verifyNoInteractions(productService);
    }
}