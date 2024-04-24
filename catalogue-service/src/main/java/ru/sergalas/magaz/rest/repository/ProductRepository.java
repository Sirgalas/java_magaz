package ru.sergalas.magaz.rest.repository;

import ru.sergalas.magaz.rest.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAll();

    Product save(Product product);

    Optional<Product> findOneById(Integer productId);

    void deleteById(Integer id);
}
