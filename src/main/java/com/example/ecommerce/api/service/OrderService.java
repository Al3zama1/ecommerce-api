package com.example.ecommerce.api.service;

import com.example.ecommerce.api.mapstruct.dto.order.OrderDto;
import com.example.ecommerce.api.mapstruct.dto.order.OrderItemDto;
import com.example.ecommerce.api.entity.Cart;
import com.example.ecommerce.api.entity.Order;
import com.example.ecommerce.api.entity.OrderItem;
import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.exception.EmptyCartException;
import com.example.ecommerce.api.exception.ExceptionMessages;
import com.example.ecommerce.api.exception.OrderNotFoundException;
import com.example.ecommerce.api.mapstruct.mappers.OrderMapper;
import com.example.ecommerce.api.repository.CartRepository;
import com.example.ecommerce.api.repository.OrderItemRepository;
import com.example.ecommerce.api.repository.OrderRepository;
import com.example.ecommerce.api.service.interfaces.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;


    @Override
    public List<OrderDto> getAllOrders(User user) {
        List<Order> orders =  orderRepository.findAllByUserOrderByDateCreatedDesc(user);

        List<OrderDto> orderDtos = new ArrayList<>();

        for (Order order : orders) {
            orderDtos.add(orderMapper.mapOrderToDto(order));
        }

        return orderDtos;
    }

    @Override
    public long createOrder(User user) {
        List<Cart> shoppingCart = cartRepository.findAllByUser(user);

        if (shoppingCart.size() == 0) throw new EmptyCartException(ExceptionMessages.CART_IS_EMPTY);

        double totalPrice = 0;
        Order order = Order.builder()
                .dateCreated(LocalDateTime.now())
                .user(user)
                .build();

        for (Cart cart : shoppingCart) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .quantity(cart.getQuantity())
                    .product(cart.getProduct())
                    .price(cart.getProduct().getPrice())
                    .build();

            order.addOderItem(orderItem);
            totalPrice += cart.getProduct().getPrice() * cart.getQuantity();
        }
        order.setTotalPrice(totalPrice);

        // make order
        order = orderRepository.save(order);
        // remove items from user's shopping cart
        cartRepository.deleteAll(shoppingCart);

        return order.getId();
    }

    @Override
    public List<OrderItemDto> getOrder(long orderId, User user) {
        Optional<Order> orderOptional = orderRepository.findByUserAndId(user, orderId);

        if (orderOptional.isEmpty()) throw new OrderNotFoundException(ExceptionMessages.ORDER_NOT_FOUND);

        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(orderOptional.get());
        List<OrderItemDto> orderItemDtos = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            orderItemDtos.add(orderMapper.mapOrderItemToDto(orderItem));
        }

        return orderItemDtos;
    }
}
