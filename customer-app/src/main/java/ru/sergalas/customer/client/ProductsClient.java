package ru.sergalas.customer.client;

import reactor.core.publisher.Flux;
import ru.sergalas.customer.entity.Product;

public interface ProductsClient {
    Flux<Product> findProducts(String filter);
}
