package ru.sergalas.customer.client.payload;

public record NewProductReviewsPayload(Integer productId, Integer rating, String review) {
}
