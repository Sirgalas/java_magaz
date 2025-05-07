package ru.sergalas.magaz.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.sergalas.magaz.rest.controller.payloads.CreateProductPayload;
import ru.sergalas.magaz.rest.entity.Product;
import ru.sergalas.magaz.rest.services.ProductService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products")

@Tag(name = "Products Controller", description = "Для работы с несколькими продуктами")
public class ProductsRestController {

    private final ProductService productService;

    @Operation(
            security = @SecurityRequirement(name = "keycloak"),

            summary = "Получить все товары",
            description = "Возращает все  товары с фильтрацией",
            parameters = {
                    @Parameter(
                        name = "filter",
                        description = "Фильтрация товара",
                        required = false
                    ),
            },
            responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Товары найден",
                        headers = @Header(name =" ContentType", description = "Тип данных"),
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                schema = @Schema(implementation = Product.class)
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "404",
                        description = "Товар не найден",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                        )
                    )
            })
    @GetMapping
    public Iterable<Product> findProducts(@RequestParam(name= "filter", required = false) String filter)
    {
        return this.productService.findAllProducts(filter);
    }

    @PostMapping
    @Operation(
            security = @SecurityRequirement(name = "keycloak"),
            summary = "Создать товар",
            description = "Создает товар",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreateProductPayload.class)
                )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Товар создан",
                            headers = @Header(name =" ContentType", description = "Тип данных"),
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Product.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка валидации",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(implementation = BindingResult.class)
                            )
                    )
            })
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody CreateProductPayload payload,
            BindingResult bindingResult,
            UriComponentsBuilder uriComponentsBuilder
    ) throws BindException
    {
        if(bindingResult.hasErrors()) {
            if(bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Product product = this.productService.createProduct(payload.title(),payload.details());
            return ResponseEntity
                .created(
                    uriComponentsBuilder
                        .replacePath("/catalogue-api/{productId}")
                        .build(Map.of("productId",product.getId())))
                .body(product);
        }
    }
}
