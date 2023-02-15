package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.dto.cart.AddToCartDto;
import com.example.ecommerce.api.dto.cart.CartDto;
import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.service.interfaces.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;

    @PostMapping
    public ResponseEntity<Void> addToCart(@Valid @RequestBody AddToCartDto cartDto) {
        long id = cartService.addProduct(cartDto);
        return ResponseEntity.created(URI.create("/api/v1/cart/" + id)).build();
    }

    @GetMapping
    public List<CartDto> getCartItems(@AuthenticationPrincipal User user) {
        return cartService.getAllCartProducts(user.getId());
    }

    @PutMapping
    public ResponseEntity<Void> updateCartItem(@Valid @RequestBody AddToCartDto addToCartDto,
                                               @AuthenticationPrincipal User user) {
        cartService.updateCartItem(addToCartDto, user.getId());
        return ResponseEntity.noContent().build();
    }
}
