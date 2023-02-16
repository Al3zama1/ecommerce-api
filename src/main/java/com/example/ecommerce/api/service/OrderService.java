package com.example.ecommerce.api.service;

import com.example.ecommerce.api.dto.order.OrderDto;
import com.example.ecommerce.api.dto.order.OrderItemDto;
import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.service.interfaces.IOrderService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderService implements IOrderService {
    @Override
    public List<OrderDto> getAllOrders(User user) {
        return null;
    }

    @Override
    public long createOrder(User user) {
        return 0;
    }

    @Override
    public List<OrderItemDto> getOrder(long orderId, User user) {
        return null;
    }
}
