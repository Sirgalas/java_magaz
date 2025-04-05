package ru.sergalas.customer.controler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.client.FavoriteProductsClient;
import ru.sergalas.customer.client.ProductsClient;
import ru.sergalas.customer.entity.FavoriteProduct;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products")
public class ProductsController {

    private final ProductsClient productClient;

    private final FavoriteProductsClient favoriteProductsClient;

    @GetMapping("list")
    public Mono<String> getListProducts(Model model, @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return  this.productClient.findProducts(filter)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("customer/products/list");
    }

    @GetMapping("favorites")
    public Mono<String> getFavoritesList(Model model, @RequestParam(name="filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return  this.favoriteProductsClient.findFavoriteProducts()
                .map(FavoriteProduct::productId)
                .collectList()
                .flatMap(favoriteProducts -> this.productClient.findProducts(filter)
                        .filter(product -> favoriteProducts.contains(product.id()))
                        .collectList()
                        .doOnNext(products -> model.addAttribute("products",products))
                        .thenReturn("customer/products/favorites")

                );
    }
}
