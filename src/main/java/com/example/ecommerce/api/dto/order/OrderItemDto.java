package com.example.ecommerce.api.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private String productName;
    private String description;
    private LocalDateTime dateCreated;
    private long quantity;
    private long orderId;
}
