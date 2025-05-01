package ru.sergalas.magaz.rest.controller.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EditProductPayload(
        @NotNull(message = "{catalogue.products.update.errors.title_is_null}")
        @Size(min = 3, max = 50, message = "{catalogue.products.update.errors.title_size_is_invalid}")
        @NotBlank(message = "{catalogue.products.create.errors.title_is_blank}")
        String title,
        @Size(max = 1000, message = "{catalogue.products.update.errors.details_size_is_invalid}")
        String details) {
}
