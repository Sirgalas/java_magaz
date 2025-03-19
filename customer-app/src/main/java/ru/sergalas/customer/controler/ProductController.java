package ru.sergalas.customer.controler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.client.ProductsClient;
import ru.sergalas.customer.entity.Product;
import ru.sergalas.customer.payload.ProductReviewPayload;
import ru.sergalas.customer.services.FavoriteProductsService;
import ru.sergalas.customer.services.ProductReviewsService;

import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("customer/product/{productId:\\d+}")
public class ProductController {
    private final ProductsClient productsClient;
    private final FavoriteProductsService favoriteProductsService;
    private final ProductReviewsService productReviewsService;

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> getProduct(@PathVariable("productId") int productId) {
        return productsClient.findProductById(productId)
                .doOnNext(body -> log.error("product: {}",body))
                .switchIfEmpty(Mono.error(new NoSuchElementException("customer.products.error.not_found")));
    }

    @GetMapping
    public Mono<String> getProduct(Model model, @PathVariable("productId") Integer productId) {
        model.addAttribute("inFavorite", false);
        return this.productReviewsService.findProductReviewsByProductId(productId)
            .collectList()
            .doOnNext(productReviews -> model.addAttribute("reviews",productReviews))
            .then(
                this.favoriteProductsService.findFavoriteProductByProduct(productId)
                    .doOnNext(favoriteProducts -> model.addAttribute("inFavorite", true))

            )
                .thenReturn("customer/products/product");

    }

    @PostMapping("add-to-favourites")
    public Mono<String> addToFavourites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
            .flatMap(product -> this.favoriteProductsService.addProductToFavorites(product.id())
                .thenReturn("redirect:/customer/product/%d".formatted(product.id()))
            );
    }

    @PostMapping("delete-from-favourites")
    public Mono<String> deleteFromFavourites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
            .flatMap(product -> this.favoriteProductsService.removeProductFromFavorites(product.id())
                .thenReturn("redirect:/customer/product/%d".formatted(product.id()))
            );
    }

    @PostMapping("create-review")
    public Mono<String> createReview(
            @PathVariable("productId") Integer productId,
            @Valid @ModelAttribute ProductReviewPayload payload,
            BindingResult bindingResult,
            Model model
            ) {

        if(bindingResult.hasErrors()) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList()
            );
            model.addAttribute("inFavorite", false);
            return this.favoriteProductsService.findFavoriteProductByProduct(productId)
                    .doOnNext(favoriteProducts -> model.addAttribute("inFavorite", true))
                    .thenReturn("customer/products/product");
        }
        return this.productReviewsService.createProductReview(productId,payload.rating(),payload.review())
                .thenReturn("redirect:/customer/product/%d".formatted(productId));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "errors/404";
    }
}
