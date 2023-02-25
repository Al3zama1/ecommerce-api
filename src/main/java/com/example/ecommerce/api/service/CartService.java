package com.example.ecommerce.api.service;

import com.example.ecommerce.api.mapstruct.dto.cart.AddToCartDto;
import com.example.ecommerce.api.mapstruct.dto.cart.CartDto;
import com.example.ecommerce.api.entity.Cart;
import com.example.ecommerce.api.entity.Product;
import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.exception.CartItemNotFoundException;
import com.example.ecommerce.api.exception.ExceptionMessages;
import com.example.ecommerce.api.exception.ProductNotFoundException;
import com.example.ecommerce.api.mapstruct.dto.cart.CartItemDto;
import com.example.ecommerce.api.mapstruct.mappers.CartMapper;
import com.example.ecommerce.api.repository.CartRepository;
import com.example.ecommerce.api.repository.ProductRepository;
import com.example.ecommerce.api.service.interfaces.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;


    @Override
    public long addProduct(AddToCartDto addToCartDto, User user) {
        // retrieve product to be added to cart
        Optional<Product> productOptional = productRepository.findById(addToCartDto.getProductId());

        if (productOptional.isEmpty()) throw new ProductNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND);

        // check if product is already in user's shopping cart
        Optional<Cart> cartOptional = cartRepository.findByUserAndProduct(user, productOptional.get());
        Cart cart;

        // product is already in users shopping cart, update its count
        if (cartOptional.isPresent()) {
            cart = cartOptional.get();
            cart.setQuantity(cart.getQuantity() + addToCartDto.getQuantity());
            cart = cartRepository.save(cart);
            return cart.getId();
        }

        cart = Cart.builder()
                .product(productOptional.get())
                .user(user)
                .quantity(addToCartDto.getQuantity())
                .dateCreated(LocalDateTime.now())
                .build();

         cart = cartRepository.save(cart);

        return cart.getId();
    }

    @Override
    public CartDto getAllCartProducts(long userId) {
        List<Cart> cartItems = cartRepository.findAllByUser_IdOrderByDateCreatedDesc(userId);
        CartDto cartDto = new CartDto();
        double totalCost = 0;

        for (Cart cart : cartItems) {
            CartItemDto cartItemDto = cartMapper.mapCartItemToDto(cart);

            cartDto.addCartItem(cartItemDto);
            totalCost += cart.getProduct().getPrice() * cart.getQuantity();
        }
        cartDto.setTotalCost(totalCost);

        return cartDto;
    }

    @Override
    public void updateCartItem(AddToCartDto addToCartDto, long cartId, User user) {
        Optional<Cart> cartOptional = cartRepository.findByIdAndUser(cartId, user);

        if (cartOptional.isEmpty()) throw new CartItemNotFoundException(ExceptionMessages.CART_ITEM_NOT_FOUND);

        Cart cart = cartOptional.get();
        cart.setQuantity(addToCartDto.getQuantity());
        cart.setDateCreated(LocalDateTime.now());

        if (cart.getQuantity() <= 0) {
            this.deleteCartItem(cartId, user);
            return;
        }

        cartRepository.save(cart);
    }

    @Override
    public void deleteCartItem(long cartId, User user) {
        Optional<Cart> cartOptional = cartRepository.findByIdAndUser(cartId, user);

        if (cartOptional.isEmpty()) throw new CartItemNotFoundException(ExceptionMessages.CART_ITEM_NOT_FOUND);

        cartRepository.delete(cartOptional.get());
    }
}
