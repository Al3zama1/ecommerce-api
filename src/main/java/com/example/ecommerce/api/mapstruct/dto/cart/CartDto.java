package com.example.ecommerce.api.mapstruct.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    private List<CartItemDto> cartItems;
    private double totalCost;

    public void addCartItem(CartItemDto cartItemDto) {
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        cartItems.add(cartItemDto);
    }
}
