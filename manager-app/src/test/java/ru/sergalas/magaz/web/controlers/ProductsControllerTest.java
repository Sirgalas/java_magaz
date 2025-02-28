package ru.sergalas.magaz.web.controlers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import ru.sergalas.magaz.web.clients.ProductsRestClient;
import ru.sergalas.magaz.web.controlers.payloads.CreateProductPayload;
import ru.sergalas.magaz.web.entity.Product;
import ru.sergalas.magaz.web.services.ProductService;
import ru.sergalas.magaz.web.services.impl.ProductServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductController")
class ProductsControllerTest {

    @Mock
    ProductsRestClient productsRestClient;
    @Mock
    ProductServiceImpl productService;
    @InjectMocks
    ProductsController controller;


    @Test
    @DisplayName("Create Product создаст новый и перенаправит на страницу товара ")
    void createProduct_RequestIsValid_ReturnRedirectionToProductPage() {
        //given
        var payload = new CreateProductPayload("Новый товар","Описание нового товара");
        var model = new ConcurrentModel();

        doReturn(new Product(1,"Новый товар","Описание нового товара"))
                .when(this.productService)
                .createProduct("Новый товар","Описание нового товара");
        //when
            var result = this.controller.createProduct(payload,model);
        //then
        assertEquals("redirect:/catalogue/products/1", result);
        verify(this.productService).createProduct("Новый товар","Описание нового товара");
        verifyNoMoreInteractions(this.productsRestClient);
    }
  
}