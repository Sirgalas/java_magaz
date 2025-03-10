package ru.sergalas.magaz.rest.controllers;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Locale;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class ProductsRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/products.sql")
    void findProduct_returnsProductsList() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products")
                .param("filter", "товар")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                        [
                             {
                                "id": 1,
                                "title": "Товар №1",
                                "details": "Описание товара №1"
                             },
                             {
                                "id": 2,
                                "title": "Товар №2",
                                "details": "Описание товара №2"
                             }
                        ]""")
                );
    }

    @Test
    void createProduct_RequestIsValid_ReturnsNewProduct() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "title": " Ещё один новый товар  №2",
                    "details": "Описание ещё одного нового товара №2"
                }
                """)
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue"))
                );
        //when
        this.mockMvc.perform(requestBuilder)
        //then
            .andDo(print())
            .andExpectAll(
                    status().isCreated(),
                    header().string(HttpHeaders.LOCATION, "http://localhost/catalogue-api/1"),
                    content().contentType(MediaType.APPLICATION_JSON),
                    content().json(""" 
                    {
                        "id": 1,
                        "title": " Ещё один новый товар  №2",
                        "details": "Описание ещё одного нового товара №2"
                    }
                    """)
            );
    }

    @Test
    void createProduct_RequestIsInValid_ReturnsProblemDetail() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "title": " ",
                    "details": null
                }
                """)
                .locale(Locale.of("ru","RU"))
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue"))
                );
        //when
        this.mockMvc.perform(requestBuilder)
        //then
            .andDo(print())
            .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                    content().json("""
                    {}
                    """)
            );
    }
    @Test
    void createProduct_UserIsNotAuthorize_ReturnsFOrbiden() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "title": " ",
                    "details": null
                }
                """)
                .locale(Locale.of("ru","RU"))
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue"))
                );
        //when
        this.mockMvc.perform(requestBuilder)
        //then
            .andDo(print())
            .andExpectAll(
                    status().isForbidden()
            );
    }
}