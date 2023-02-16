package com.example.ecommerce.api.service;

import com.example.ecommerce.api.dto.cart.AddToCartDto;
import com.example.ecommerce.api.dto.cart.CartDto;
import com.example.ecommerce.api.service.interfaces.ICartService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService implements ICartService {
    @Override
    public long addProduct(AddToCartDto addToCartDto) {
        return 0;
    }

    @Override
    public List<CartDto> getAllCartProducts(long userId) {
        return null;
    }

    @Override
    public void updateCartItem(AddToCartDto addToCartDto, long userId) {

    }
}
