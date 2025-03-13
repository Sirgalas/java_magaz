package ru.sergalas.customer.controler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;
import ru.sergalas.customer.client.ProductsClient;
import ru.sergalas.customer.entity.Product;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("customer/product/{productId:\\d+}")
public class ProductController {
    private final ProductsClient productsClient;

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> getProduct(@PathVariable("productId") int productId) {
        return productsClient.findProductById(productId).doOnNext(body -> log.error("product: {}",body));
    }

    @GetMapping
    public String getProduct() {

        return "customer/products/product";
    }
}
