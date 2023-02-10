package com.example.ecommerce.api.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private String imageUrl;
    @PositiveOrZero
    private long stockQuantity;
    @PositiveOrZero
    private double price;
}
