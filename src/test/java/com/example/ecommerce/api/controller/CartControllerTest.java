package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.config.JwtService;
import com.example.ecommerce.api.config.SecurityConfig;
import com.example.ecommerce.api.config.UserAuthenticationEntryPoint;
import com.example.ecommerce.api.config.WebSecurity;
import com.example.ecommerce.api.mapstruct.dto.cart.AddToCartDto;
import com.example.ecommerce.api.mapstruct.dto.cart.CartDto;
import com.example.ecommerce.api.mapstruct.dto.cart.CartItemDto;
import com.example.ecommerce.api.mapstruct.dto.product.ProductResponseDto;
import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.entity.UserRole;
import com.example.ecommerce.api.repository.UserRepository;
import com.example.ecommerce.api.service.interfaces.ICartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;

import static com.example.ecommerce.api.ExceptionBodyResponseMatcher.exceptionMatcher;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import({WebSecurity.class, SecurityConfig.class, JwtService.class, UserAuthenticationEntryPoint.class})
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ICartService cartService;
    @MockBean
    UserRepository userRepository;
    private static PodamFactory podamFactory;

    @BeforeAll
    static void setUp() {
        podamFactory = new PodamFactoryImpl();
    }

    private static final String USERNAME = "john@gmail.com";


    // tests to add product to cart
    @Test
    void shouldAddItemToCartAndReturnLocationWhenValidInput() throws Exception {
        // Given
        AddToCartDto cartItem = AddToCartDto.builder()
                .productId(1L)
                .quantity(1)
                .build();
        User user = User.builder()
                .role(UserRole.USER)
                .build();

        given(cartService.addProduct(cartItem, user)).willReturn(1L);

        // When
        mockMvc.perform(post("/api/v1/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItem))
                .with(user(user)))
                .andExpect(header().string("Location", "/api/v1/cart/1"))
                .andExpect(status().isCreated());

        // Then
        then(cartService).should().addProduct(cartItem, user);
    }

    @Test
    void shouldReturn422WhenInvalidAddItemToCartInputIsProvided() throws Exception {
        // Given
        AddToCartDto cartItem = AddToCartDto.builder()
                .productId(-1)
                .build();

        // When
        mockMvc.perform(post("/api/v1/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItem))
                .with(user(USERNAME)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(exceptionMatcher()
                        .containsError("productId", "must be greater than or equal to 0"))
                .andExpect(exceptionMatcher()
                        .containsError("quantity", "must be greater than 0"));

        // Then
        then(cartService).shouldHaveNoInteractions();
    }

    @Test
    void shouldNotAccessAddItemToCartEndpointWhenUserIsUnauthenticated() throws Exception {
        // Given
        AddToCartDto cartItem = AddToCartDto.builder()
                .productId(1)
                .quantity(1)
                .build();

        // When
        mockMvc.perform(post("/api/v1/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItem)))
                .andExpect(status().isUnauthorized());

        // Then
        then(cartService).shouldHaveNoInteractions();
    }


    // tests to get all cart items
    @Test
    void shouldReturnAllCartItemsWhenUserIsAuthenticated() throws Exception {
        // Given
        User user = getUser();
        ProductResponseDto productResponseDto = podamFactory.manufacturePojo(ProductResponseDto.class);

        CartItemDto cartItem = CartItemDto.builder()
                .id(1)
                .product(productResponseDto)
                .quantity(2)
                .build();

        CartDto cart = CartDto.builder()
                .cartItems(List.of(cartItem))
                .totalCost(cartItem.getQuantity() * productResponseDto.getPrice())
                .build();

        given(cartService.getAllCartProducts(user.getId())).willReturn(cart);

        // When
        mockMvc.perform(get("/api/v1/cart")
                .with(user(user)))
                .andExpect(jsonPath("$.cartItems.size()", Matchers.is(1)))
                .andExpect(status().isOk());

        // Then
        then(cartService).should().getAllCartProducts(user.getId());
    }

    @Test
    void shouldNotGetCartItemsWhenUserIsUnauthenticated() throws Exception {
        // Given

        // When
        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isUnauthorized());

        // Then
        then(cartService).shouldHaveNoInteractions();
    }

    // tests to update cart item
    @Test
    void shouldCallBusinessLogicToUpdateCartItem() throws Exception {
        // Given
        User user = getUser();
        AddToCartDto cartItem = AddToCartDto.builder()
                .productId(1)
                .quantity(3)
                .build();

        // When
        mockMvc.perform(put("/api/v1/cart/{cardId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItem))
                .with(user(user)))
                .andExpect(status().isNoContent());

        // Then
        then(cartService).should().updateCartItem(cartItem, 1L, user);
    }

    @Test
    void shouldFailCartUpdateWhenUserIsUnauthenticated() throws Exception {
        // Given
        long cartId = 1L;
        AddToCartDto cartItem = podamFactory.manufacturePojo(AddToCartDto.class);

        // When
        mockMvc.perform(put("/api/v1/cart/{cartId}", cartId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItem)))
                .andExpect(status().isUnauthorized());

        // Then
        then(cartService).shouldHaveNoInteractions();
    }

    @Test
    void shouldCallBusinessLogicToDeleteCartItem() throws Exception {
        // Given
        long cartId = 1L;
        User user = getUser();

        // When
        mockMvc.perform(delete("/api/v1/cart/{cartId}", cartId)
                .with(user(user)))
                .andExpect(status().isNoContent());

        // Then
        then(cartService).should().deleteCartItem(cartId, user);
    }

    @Test
    void shouldNotDeleteCartItemWhenUserIsUnauthenticated() throws Exception {
        // Given
        long cartId = 1L;

        // When
        mockMvc.perform(delete("/api/v1/cart/{cartId}", cartId))
                .andExpect(status().isUnauthorized());

        // Then
        then(cartService).shouldHaveNoInteractions();
    }

    @Test
    void shouldNotDeleteCartItemWhenInvalidCartIdIsGiven() throws Exception {
        // Given
        long cartId = -1L;
        User user = getUser();

        // When
        mockMvc.perform(delete("/api/v1/cart/{cartId}", cartId)
                .with(user(user)))
                .andExpect(status().isUnprocessableEntity());

        // Then
        then(cartService).shouldHaveNoInteractions();
    }


    private User getUser() {
        return User.builder()
                .id(1L)
                .role(UserRole.USER)
                .build();
    }



}