package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.mapstruct.dto.cart.AddToCartDto;
import com.example.ecommerce.api.mapstruct.dto.cart.CartDto;
import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.service.interfaces.ICartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Validated
public class CartController {
    private final ICartService cartService;

    @PostMapping
    public ResponseEntity<Void> addToCart(@Valid @RequestBody AddToCartDto cartDto,
                                          @AuthenticationPrincipal User user) {
        long id = cartService.addProduct(cartDto, user);
        return ResponseEntity.created(URI.create("/api/v1/cart/" + id)).build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CartDto getCartItems(@AuthenticationPrincipal User user) {
        return cartService.getAllCartProducts(user.getId());
    }

    @PutMapping("/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCartItem(@Valid @RequestBody AddToCartDto addToCartDto,
                                               @PathVariable long cartId,
                                               @AuthenticationPrincipal User user) {
        cartService.updateCartItem(addToCartDto, cartId, user);
    }

    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItem(@PositiveOrZero @PathVariable long cartId,
                                               @AuthenticationPrincipal User user) {
        cartService.deleteCartItem(cartId, user);
    }
}
