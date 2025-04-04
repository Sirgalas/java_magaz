package ru.sergalas.feedback.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview {
    private UUID id;

    private Integer productId;

    private Integer rating;

    private String review;
}
