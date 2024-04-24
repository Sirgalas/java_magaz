package ru.sergalas.magaz.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import ru.sergalas.magaz.rest.controller.payloads.EditProductPayload;
import ru.sergalas.magaz.rest.entity.Product;
import ru.sergalas.magaz.rest.services.ProductService;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products/{productId:\\d+}")
public class ProductRestController {

    private final ProductService productService;

    @ModelAttribute("product")
    public Product getProduct(@PathVariable ("productId") int productId) {
        return this.productService.findProduct(productId)
                .orElseThrow(() -> new NoSuchMessageException(""));
    }

    @GetMapping
    public Product findProduct(@ModelAttribute("product") Product product) {
        return product;
    }

    @PatchMapping
    public ResponseEntity<?> updateProduct(
            @PathVariable("productID") Integer productId,
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
}
