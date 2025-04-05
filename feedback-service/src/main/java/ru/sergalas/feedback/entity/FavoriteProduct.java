package ru.sergalas.feedback.entity;

import lombok.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteProduct {
    private UUID id;
    private Integer productId;
}
