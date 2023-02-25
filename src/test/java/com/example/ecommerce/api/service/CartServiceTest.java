package com.example.ecommerce.api.service;

import com.example.ecommerce.api.mapstruct.dto.cart.AddToCartDto;
import com.example.ecommerce.api.entity.Cart;
import com.example.ecommerce.api.entity.Product;
import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.entity.UserRole;
import com.example.ecommerce.api.exception.CartItemNotFoundException;
import com.example.ecommerce.api.exception.ExceptionMessages;
import com.example.ecommerce.api.exception.ProductNotFoundException;
import com.example.ecommerce.api.mapstruct.mappers.CartMapper;
import com.example.ecommerce.api.repository.CartRepository;
import com.example.ecommerce.api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartMapper cartMapper;
    @InjectMocks
    private CartService cut;
    private static ArgumentCaptor<Cart> cartArgumentCaptor;
    private static PodamFactory podamFactory;

    @BeforeAll
    static void setUp() {
        cartArgumentCaptor = ArgumentCaptor.forClass(Cart.class);
        podamFactory = new PodamFactoryImpl();
    }

    // tests to return all user's shopping cart items
    @Test
    void shouldReturnAllUserShoppingCartItems() {
        // Given
        long userId = 1L;

        // When
        cut.getAllCartProducts(userId);

        // Then
        then(cartRepository).should().findAllByUser_IdOrderByDateCreatedDesc(userId);
    }

    // tests to add product to shopping user's shopping cart
    @Test
    void shouldAddProductToShoppingCartIfIsNotThere() {
        // Given
        AddToCartDto addToCartDto = podamFactory.manufacturePojo(AddToCartDto.class);
        Product product = podamFactory.manufacturePojo(Product.class);
        product.setId(addToCartDto.getProductId());

        User user = User.builder()
                .id(1L)
                .role(UserRole.USER)
                .build();

        given(productRepository.findById(addToCartDto.getProductId())).willReturn(Optional.of(product));
        given(cartRepository.findByUserAndProduct(user, product)).willReturn(Optional.empty());
        given(cartRepository.save(any(Cart.class))).willAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            savedCart.setId(1L);
            return savedCart;
        });

        // When
        long cartId = cut.addProduct(addToCartDto, user);

        // Then
        then(cartRepository).should().save(any(Cart.class));
        assertThat(cartId).isEqualTo(1L);

    }

    @Test
    void shouldIncrementProductCountIfAlreadyInShoppingCart() {
        // Given
        AddToCartDto addToCartDto = podamFactory.manufacturePojo(AddToCartDto.class);
        Product product = podamFactory.manufacturePojo(Product.class);
        product.setId(addToCartDto.getProductId());
        User user = User.builder()
                .id(1L)
                .role(UserRole.USER)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .build();

        given(productRepository.findById(addToCartDto.getProductId())).willReturn(Optional.of(product));
        given(cartRepository.findByUserAndProduct(user, product)).willReturn(Optional.of(cart));
        given(cartRepository.save(cart)).willAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            savedCart.setId(1L);
            return savedCart;
        });

        // When
        long cartId = cut.addProduct(addToCartDto, user);

        // Then
        then(cartRepository).should().save(cart);

    }
    @Test
    void shouldThrowProductNotFoundExceptionWhenAddingNonExistingProduct() {
        // Given
        AddToCartDto addToCartDto = AddToCartDto.builder()
                        .productId(1L)
                        .build();
        User user = User.builder().build();
        given(productRepository.findById(1L)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.addProduct(addToCartDto, user))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage(ExceptionMessages.PRODUCT_NOT_FOUND);

        // Then
        then(cartRepository).shouldHaveNoInteractions();
    }

    // tests to update user's shopping cart
    @Test
    void shouldUpdateUserShoppingCartItemWhenItIsPresent() {
        // Given
        AddToCartDto addToCartDto = AddToCartDto.builder()
                .productId(1L)
                .quantity(4)
                .build();
        long cartId = 1L;
        User user = User.builder()
                .role(UserRole.USER)
                .build();

        Cart cart = Cart.builder()
                .id(cartId)
                .build();

        given(cartRepository.findByIdAndUser(cartId, user)).willReturn(Optional.of(cart));

        // When
        cut.updateCartItem(addToCartDto, cartId, user);

        // Then
        then(cartRepository).should().save(cartArgumentCaptor.capture());
        Cart savedCart = cartArgumentCaptor.getValue();
        assertThat(savedCart.getQuantity()).isEqualTo(addToCartDto.getQuantity());
    }

    @Test
    void shouldDeleteCartItemWhenUpdatingCountToZeroOrLess() {
        // Given
        AddToCartDto addToCartDto = AddToCartDto.builder()
                .productId(1L)
                .quantity(-4)
                .build();
        long cartId = 1L;
        User user = User.builder()
                .role(UserRole.USER)
                .build();

        Cart cart = Cart.builder()
                .id(cartId)
                .build();

        given(cartRepository.findByIdAndUser(cartId, user)).willReturn(Optional.of(cart));

        // When
        cut.updateCartItem(addToCartDto, cartId, user);

        // Then
        then(cartRepository).should(never()).save(any());
    }

    @Test
    void shouldThrowNotInShoppingCartExceptionWhenItemNotInUserShoppingCart() {
        // Given
        User user = User.builder().build();
        long cartId = 1L;
        AddToCartDto addToCartDto = AddToCartDto.builder().build();

        given(cartRepository.findByIdAndUser(cartId, user)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.updateCartItem(addToCartDto, cartId, user))
                .isInstanceOf(CartItemNotFoundException.class)
                .hasMessage(ExceptionMessages.CART_ITEM_NOT_FOUND);

        // Then
        then(cartRepository).should(never()).save(any(Cart.class));
    }

    @Test
    void shouldDeleteCartItemWhenItExists() {
        // Given
        long cartId = 1L;
        User user = User.builder().build();
        Cart cart = Cart.builder().build();

        given(cartRepository.findByIdAndUser(cartId, user)).willReturn(Optional.of(cart));

        // When
        cut.deleteCartItem(cartId, user);

        // Then
        then(cartRepository).should().delete(cart);
    }

    @Test
    void shouldThrowCartItemNotFoundExceptionWhenDeletingItemNotInCart() {
        // Given
        long cartId = 1L;
        User user = User.builder().build();

        given(cartRepository.findByIdAndUser(cartId, user)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.deleteCartItem(cartId, user))
                .isInstanceOf(CartItemNotFoundException.class)
                .hasMessage(ExceptionMessages.CART_ITEM_NOT_FOUND);

        // Then
        then(cartRepository).should(never()).delete(any());
    }


}