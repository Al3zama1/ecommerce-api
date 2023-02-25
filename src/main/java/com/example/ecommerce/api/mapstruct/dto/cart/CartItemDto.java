package com.example.ecommerce.api.mapstruct.dto.cart;

import com.example.ecommerce.api.mapstruct.dto.product.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private long id;
    private ProductResponseDto product;
    private long quantity;
}
