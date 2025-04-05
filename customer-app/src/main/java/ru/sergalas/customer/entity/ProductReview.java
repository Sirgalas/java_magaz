package ru.sergalas.customer.entity;

import java.util.UUID;

public record ProductReview(UUID id, Integer productId, Integer rating, String review) {
}
