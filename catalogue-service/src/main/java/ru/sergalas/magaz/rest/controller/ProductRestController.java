package ru.sergalas.magaz.rest.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.sergalas.magaz.rest.controller.payloads.EditProductPayload;
import ru.sergalas.magaz.rest.entity.Product;
import ru.sergalas.magaz.rest.services.ProductService;

import java.util.Locale;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products/{productId:\\d+}")
@Tag(name = "Product Controller", description = "Для работы с одним продуктом")
public class ProductRestController {
    private final ProductService productService;
    private final MessageSource messageSource;


    @ModelAttribute("product")
    @Parameter(hidden = true)
    public Product getProduct(@PathVariable ("productId") int productId) {
        return this.productService.findProduct(productId)
                .orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
    }

    @Operation(
        security = @SecurityRequirement(name = "keycloak"),
        summary = "Получить товар по id",
        description = "Возращает товар по его идентификатору",
        parameters = {
            @Parameter(
                name = "productId",
                description = "Идентификатор товара",
                example = "1",
                required = true,
                in = ParameterIn.PATH
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Товар найден",
                headers = @Header(name =" ContentType", description = "Тип данных"),
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = Product.class)
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
    public Product findProduct(@Parameter(hidden = true) @ModelAttribute("product") Product product) {
        return product;
    }


    @PatchMapping
    public ResponseEntity<?> updateProduct(
            @PathVariable("productId") Integer productId,
            @Valid @RequestBody EditProductPayload payload,
            BindingResult bindingResult
    )  throws BindException  {
        if(bindingResult.hasErrors()) {
            if(bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            this.productService.updateProduct(productId, payload.title(),payload.details());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Integer productId) {
        this.productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

     @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(
            NoSuchElementException exception,
            Locale locale
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                this.messageSource.getMessage(
                    exception.getMessage(),
                    new Object[0],
                    exception.getMessage(),
                    locale
                )
            )
        );
    }
}
