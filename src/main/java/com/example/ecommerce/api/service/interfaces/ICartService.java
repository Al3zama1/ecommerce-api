package com.example.ecommerce.api.service.interfaces;

import com.example.ecommerce.api.dto.cart.AddToCartDto;
import com.example.ecommerce.api.dto.cart.CartDto;

import java.util.List;

public interface ICartService {

    long addProduct(AddToCartDto addToCartDto);

    List<CartDto> getAllCartProducts(long userId);

    void updateCartItem(AddToCartDto addToCartDto, long userId);
}
