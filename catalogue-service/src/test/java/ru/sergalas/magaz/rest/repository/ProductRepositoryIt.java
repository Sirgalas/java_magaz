package ru.sergalas.magaz.rest.repository;

import jakarta.transaction.Transactional;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.sergalas.magaz.rest.entity.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/sql/products.sql")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryIt {
    @Autowired
    ProductRepository productRepository;

    @Test
    void findAllByTitleLikeIgnoreCase_ReturnsFilteredProductList() {
        //given
        var filter = "%Кефирка%бутыла%";

        //when
         var products = productRepository.findAllByTitleLikeIgnoreCase(filter);
        //then
        assertEquals(List.of(new Product(4,"Кефирка бутыла","Жирность 3,2%")),products);
    }
}