package com.example.ecommerce.api.service.interfaces;

import com.example.ecommerce.api.mapstruct.dto.cart.AddToCartDto;
import com.example.ecommerce.api.mapstruct.dto.cart.CartDto;
import com.example.ecommerce.api.entity.User;

public interface ICartService {

    long addProduct(AddToCartDto addToCartDto, User user);

    CartDto getAllCartProducts(long userId);

    void updateCartItem(AddToCartDto addToCartDto, long cartId, User user);

    void deleteCartItem(long cartId, User user);
}
