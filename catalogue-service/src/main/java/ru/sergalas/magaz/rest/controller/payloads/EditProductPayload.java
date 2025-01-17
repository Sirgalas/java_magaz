package ru.sergalas.magaz.rest.controller.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EditProductPayload(
    @NotNull
    @Size(min = 3, max = 50)
    String title,
    @Size(max = 1000)
    String details) {
}
