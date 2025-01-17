package ru.sergalas.magaz.web.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.sergalas.magaz.web.controlers.payloads.CreateProductPayload;
import ru.sergalas.magaz.web.controlers.payloads.EditProductPayload;
import ru.sergalas.magaz.web.entity.Product;
import ru.sergalas.magaz.web.exeption.BadRequestException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
public class ProductsRestClientImpl implements  ProductsRestClient{

    private static final ParameterizedTypeReference<List<Product>> PRODUCT_TYPE_REFERENCE = new ParameterizedTypeReference<>() {};

    private final RestClient restClient;

    @Override
    public List<Product> findAllProducts(String filter) {
        return this.restClient
            .get()
            .uri("/catalogue-api/products?filter={filter}", filter)
            .retrieve()
            .body(PRODUCT_TYPE_REFERENCE);
    }

    @Override
    public Product createProduct(String title, String details) {
        return this.restClient
            .post()
            .uri("/catalogue-api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new CreateProductPayload(title,details))
            .retrieve()
            .body(Product.class);
    }

    @Override
    public Optional<Product> findProduct(int productId) {
        return Optional.ofNullable(
            this.restClient
                .get()
                .uri("catalogue-api/products/{productId}",productId)
                .retrieve()
                .body(Product.class)
        );
    }

    @Override
    public void updateProduct(int productId, String title, String details) {
       try{
           this.restClient
               .patch()
               .uri("/catalogue-api/products/{productId}", productId)
               .contentType(MediaType.APPLICATION_JSON)
               .body(new EditProductPayload(title,details))
               .retrieve()
               .toBodilessEntity();
       } catch (HttpClientErrorException.BadRequest exception) {
           ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
           throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
       }

    }

    @Override
    public void deleteProduct(int productId) {
        try {
            this.restClient
                .delete()
                    .uri("/catalogue-api/products/{productId}", productId)
                .retrieve()
                .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception);
        }
    }
}
