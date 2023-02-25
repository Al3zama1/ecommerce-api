package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.mapstruct.dto.order.OrderDto;
import com.example.ecommerce.api.mapstruct.dto.order.OrderItemDto;
import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.service.interfaces.IOrderService;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final IOrderService orderService;

    @GetMapping
    public List<OrderDto> getAllOrders(@AuthenticationPrincipal User user) {
        return orderService.getAllOrders(user);
    }

    @GetMapping("/{orderId}")
    public List<OrderItemDto> getOrder(@AuthenticationPrincipal User user,
                                       @PositiveOrZero @PathVariable long orderId) {
        return orderService.getOrder(orderId, user);
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(@AuthenticationPrincipal User user) {
        long id = orderService.createOrder(user);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + id)).build();
    }
}
