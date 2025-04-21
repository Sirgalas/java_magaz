package ru.sergalas.feedback.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("favourite_product")
public class FavoriteProduct {
    @Id
    private UUID id;
    private Integer productId;
    private String userId;
}
