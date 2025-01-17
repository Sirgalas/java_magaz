package ru.sergalas.magaz.web.controlers.payloads;

public record CreateProductPayload(
        String title,
        String details
) {
}
