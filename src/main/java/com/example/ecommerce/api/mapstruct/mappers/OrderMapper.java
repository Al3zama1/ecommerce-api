package com.example.ecommerce.api.mapstruct.mappers;

import com.example.ecommerce.api.entity.Order;
import com.example.ecommerce.api.entity.OrderItem;
import com.example.ecommerce.api.mapstruct.dto.order.OrderDto;
import com.example.ecommerce.api.mapstruct.dto.order.OrderItemDto;
import org.mapstruct.Mapper;

@Mapper
public interface OrderMapper {

    OrderDto mapOrderToDto(Order order);
    OrderItemDto mapOrderItemToDto(OrderItem orderItem);
}
