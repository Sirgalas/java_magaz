package ru.sergalas.feedback.payload;

import jakarta.validation.constraints.NotNull;

public record NewFavoriteProductPayload(
        @NotNull(message = "{customer.products.favorites.create.errors.product_id_is_null}")
        Integer productId) {
}
