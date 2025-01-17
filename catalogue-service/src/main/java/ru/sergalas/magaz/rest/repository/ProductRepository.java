package ru.sergalas.magaz.rest.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.sergalas.magaz.rest.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    //@Query(value = "select p from Product p where p.title ilike :filter") JPQL
   // @Query(value = "select * from catalogue.product where title ilike :filter", nativeQuery = true)
    @Query(name = "Product.findAllByTitleLikeIgnoringCase")
    Iterable<Product> findAllByTitleLikeIgnoreCase(@Param("filter") String filter);
}
