package ru.sergalas.magaz.web.controlers;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.sergalas.magaz.web.entity.Product;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WireMockTest(httpPort = 54321)
public class ProductsControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Страница созданиея товара")
    void getNewProductPage_ReturnProductPage() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/create")
                .with(user("test").roles("MANAGER"));

        //when
        this.mockMvc.perform(requestBuilder)
        //then
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/new")
                );
    }

    @Test
    void getProductList_ReturnProductListPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/list")
                .queryParam("filter","Товар")
                .with(user("test").roles("MANAGER"));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products"))
            .withQueryParam("filter",WireMock.equalTo("Товар"))
            .willReturn(WireMock.ok("""
                [
                    {"id": 1,"title":"Товар №1","details":"Описание товара №1"},
                    {"id": 2,"title":"Товар №2","details":"Описание товара №2"}
                ]""")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            )
        );
        //when
        this.mockMvc.perform(requestBuilder)
        // then
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                        view().name("catalogue/products/list"),
                        model().attribute("filter","Товар"),
                        model().attribute("products", List.of(
                               new Product(1,"Товар №1","Описание товара №1"),
                               new Product(2,"Товар №2","Описание товара №2")
                        ))
                );
        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products")).withQueryParam("filter",WireMock.equalTo("Товар")));
    }
}
