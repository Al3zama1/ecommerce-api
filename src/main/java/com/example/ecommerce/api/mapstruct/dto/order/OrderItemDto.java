package com.example.ecommerce.api.mapstruct.dto.order;

import com.example.ecommerce.api.mapstruct.dto.product.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private long id;
    private ProductResponseDto product;
    private long quantity;

}
