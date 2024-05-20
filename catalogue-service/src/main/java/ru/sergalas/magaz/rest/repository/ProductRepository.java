package ru.sergalas.magaz.rest.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sergalas.magaz.rest.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Integer> {

}
