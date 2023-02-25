package com.example.ecommerce.api.mapstruct.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private long id;
    private LocalDateTime dateCreated;
    private double totalPrice;
}
