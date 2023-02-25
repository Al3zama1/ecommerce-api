package com.example.ecommerce.api.service.interfaces;

import com.example.ecommerce.api.mapstruct.dto.order.OrderDto;
import com.example.ecommerce.api.mapstruct.dto.order.OrderItemDto;
import com.example.ecommerce.api.entity.User;

import java.util.List;

public interface IOrderService {

    List<OrderDto> getAllOrders(User user);

    long createOrder(User user);

    List<OrderItemDto> getOrder(long orderId, User user);
}
