package ru.sergalas.magaz.rest.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ProductRestControllerITest {

    @Autowired
    MockMvc mockMvc;


    @Test
    @Sql("/sql/products.sql")
    void findProduct_ProductExist_ReturnProduct() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products/1")
                .with(jwt().jwt(builder -> builder.claim("scope","view_catalogue")));
        //when
        this.mockMvc
            .perform(requestBuilder)
        //then
            .andDo(print())
            .andExpectAll(
                status().isOk(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                content().json("""
                  {
                    "id": 1,
                    "title": "Товар №1",
                    "details": "Описание товара №1"
                  }
                """)
            );
    }

    @Test
    void findProduct_ProductNotExist_ReturnNotFound() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products/1")
            .with(jwt().jwt(builder -> builder.claim("scope","view_catalogue")));
        //when
        this.mockMvc
            .perform(requestBuilder)
        //then
            .andDo(print())
                .andExpectAll(
                        status().isNotFound()
                );
    }


    @Test
    @Sql("/sql/products.sql")
    void findProduct_UserIsNotAuthorized_ReturnForbidden() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products/1")
                .with(jwt());
        //when
        this.mockMvc
            .perform(requestBuilder)
            //then
            .andDo(print())
            .andExpectAll(
                status().isForbidden()
            );
    }


    @Test
    @Sql("/sql/products.sql")
    void updateProduct_RequestIsValid_ReturnNoContent() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/products/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
            {
              "title": "новый товар",
              "details": "Описание нового товара"
            }
            """)
            .with(jwt().jwt(builder -> builder.claim("scope","edit_catalogue")));
        //when
        this.mockMvc
            .perform(requestBuilder)
            //then
            .andDo(print())
            .andExpectAll(
                    status().isNoContent()
            );
    }

    @Test
    @Sql("/sql/products.sql")
    void updateProduct_RequestIsInValid_ReturnBadRequest() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/products/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
            {
              "title": "  ",
              "details": null
            }
            """)
            .with(jwt().jwt(builder -> builder.claim("scope","edit_catalogue")));
        //when
        this.mockMvc
            .perform(requestBuilder)
            //then
            .andDo(print())
            .andExpectAll(
                status().isBadRequest(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                content().json(""" 
                {
                  "errors":[
                    "{catalogue.products.create.errors.title_is_blank}",
                    "{catalogue.products.update.errors.title_size_is_invalid}"
                  ]
                }
                """)
            );
    }

    @Test
    void updateProduct_RequestDoesNotExist_ReturnNotFound() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                  "title": "новый товар",
                  "details": "Описание нового товара"
                }
                """)
                .with(jwt().jwt(builder -> builder.claim("scope","edit_catalogue")));
        //when
        this.mockMvc
                .perform(requestBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void updateProduct_UserIsNotAuth_ReturnForbidden() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.patch("/catalogue-api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                  "title": "новый товар",
                  "details": "Описание нового товара"
                }
                """)
                .with(jwt());
        //when
        this.mockMvc
            .perform(requestBuilder)
            //then
            .andDo(print())
            .andExpectAll(
                    status().isForbidden()
            );
    }

    @Test
    @Sql("/sql/products.sql")
    void deleteProduct_RequestExists_ReturnNoContent() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.delete("/catalogue-api/products/1")
            .with(jwt().jwt(builder -> builder.claim("scope","edit_catalogue")));
        //when
        this.mockMvc
            .perform(requestBuilder)
            //then
            .andDo(print())
            .andExpectAll(
                status().isNoContent()
            );
    }

    @Test
    void deleteProduct_ProductDoesNotExists_ReturnNoContent() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.delete("/catalogue-api/products/1")
            .with(jwt().jwt(builder -> builder.claim("scope","edit_catalogue")));
        //when
        this.mockMvc
            .perform(requestBuilder)
            //then
            .andDo(print())
            .andExpectAll(
                status().isNotFound()
            );
    }

    @Test
    @Sql("/sql/products.sql")
    void deleteProduct_ProductUser_ReturnNoContent() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.delete("/catalogue-api/products/1")
            .with(jwt());
        //when
        this.mockMvc
            .perform(requestBuilder)
            //then
            .andDo(print())
            .andExpectAll(
                status().isForbidden()
            );
    }

}