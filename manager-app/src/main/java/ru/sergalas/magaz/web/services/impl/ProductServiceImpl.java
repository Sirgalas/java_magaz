package ru.sergalas.magaz.web.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.sergalas.magaz.web.clients.ProductsRestClient;
import ru.sergalas.magaz.web.entity.Product;
import ru.sergalas.magaz.web.exeption.BadRequestException;
import ru.sergalas.magaz.web.services.ProductService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    final private ProductsRestClient productsRestClient;

    @Override
    public Iterable<Product> findAllProducts(String filter) {
        return this.productsRestClient.findAllProducts(filter);
    }

    @Override
    public Product createProduct(String title, String details) {
        try{
            return this.productsRestClient.createProduct(title,details);
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public Optional<Product> findProduct(int productId) {
        try{
            return this.productsRestClient.findProduct(productId);
        } catch ( HttpClientErrorException.NotFound exception) {
            return Optional.empty();
        }
    }

    @Override
    public void updateProduct(int productId, String title, String details) {
        try {
            this.productsRestClient.updateProduct(productId,title,details);
        }catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try{
            this.productsRestClient.deleteProduct(productId);
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception);
        }
    }
}
