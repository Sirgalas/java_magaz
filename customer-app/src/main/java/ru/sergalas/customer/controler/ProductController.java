package ru.sergalas.customer.controler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.client.FavoriteProductsClient;
import ru.sergalas.customer.client.ProductReviewsClient;
import ru.sergalas.customer.client.ProductsClient;
import ru.sergalas.customer.client.exeception.ClientBadRequestException;
import ru.sergalas.customer.entity.Product;
import ru.sergalas.customer.payload.ProductReviewPayload;

import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("customer/product/{productId:\\d+}")
public class ProductController {
    private final ProductsClient productsClient;
    private final FavoriteProductsClient favoriteProductsService;
    private final ProductReviewsClient productReviewsClient;

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> getProduct(@PathVariable("productId") int productId) {
        return productsClient.findProductById(productId)
            .doOnNext(body -> log.error("product: {}",body))
            .switchIfEmpty(Mono.defer(
                    ()->Mono.error(new NoSuchElementException("customer.products.error.not_found"))
                )
            );
    }

    @GetMapping
    public Mono<String> getProduct(Model model, @PathVariable("productId") Integer productId) {
        model.addAttribute("inFavorite", false);
        return this.productReviewsClient.findProductReviewsByProductId(productId)
            .collectList()
            .doOnNext(productReviews -> model.addAttribute("reviews",productReviews))
            .then(
                this.favoriteProductsService.findFavoriteProductByProductId(productId)
                    .doOnNext(favoriteProducts -> model.addAttribute("inFavorite", true))

            )
                .thenReturn("customer/products/product");

    }

    @PostMapping("add-to-favourites")
    public Mono<String> addToFavourites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
            .flatMap(product -> this.favoriteProductsService.addProductToFavorites(product.id())
                .thenReturn("redirect:/customer/product/%d".formatted(product.id()))
                .onErrorResume(ex -> {
                    log.error(ex.getMessage(), ex);
                    return Mono.just("redirect:/customer/product/%d".formatted(product.id()));
                })
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
            @ModelAttribute ProductReviewPayload payload,
            Model model
            ) {
        return this.productReviewsClient.createProductReview(productId,payload)
            .thenReturn("redirect:/customer/product/%d".formatted(productId))
            .onErrorResume(
                ClientBadRequestException.class,
                exception -> {
                    log.error(exception.getMessage(), exception);
                    model.addAttribute("errors", exception.getErrorMessages());
                    model.addAttribute("inFavorite", false);
                    return this.
                        favoriteProductsService
                        .findFavoriteProductByProductId(productId)
                        .doOnNext(favoriteProducts -> model.addAttribute("inFavorite", true))
                        .thenReturn("customer/products/product");
            });
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(
            NoSuchElementException e,
            Model model,
            ServerHttpResponse response
    ) {

        model.addAttribute("error", e.getMessage());
        response.setStatusCode(HttpStatus.NOT_FOUND);
        return "errors/404";
    }

    @ModelAttribute
    public Mono<CsrfToken> loadCsrfToken(ServerWebExchange exchange) {
        return exchange.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName())
            .doOnSuccess(token -> exchange.getAttributes()
                .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token)
            );
    }
}
