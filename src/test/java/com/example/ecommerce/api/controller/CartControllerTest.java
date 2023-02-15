package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.config.JwtService;
import com.example.ecommerce.api.config.SecurityConfig;
import com.example.ecommerce.api.config.UserAuthenticationEntryPoint;
import com.example.ecommerce.api.config.WebSecurity;
import com.example.ecommerce.api.dto.cart.AddToCartDto;
import com.example.ecommerce.api.dto.cart.CartDto;
import com.example.ecommerce.api.dto.cart.CartItemDto;
import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.entity.UserRole;
import com.example.ecommerce.api.repository.UserRepository;
import com.example.ecommerce.api.service.interfaces.ICartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private static final String USERNAME = "john@gmail.com";


    // tests to add product to cart
    @Test
    void shouldCallBusinessLogicToAddItemToCartAndReturnLocation() throws Exception {
        // Given
        AddToCartDto cartItem = AddToCartDto.builder()
                .productId(1L)
                .quantity(1)
                .build();

        given(cartService.addProduct(cartItem)).willReturn(1L);

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItem))
                .with(user(USERNAME)))
                .andExpect(status().isCreated()).andReturn();

        // Then
        then(cartService).should().addProduct(cartItem);
        assertThat(result.getResponse().getHeader("Location")).isEqualTo("/api/v1/cart/1");
    }

    @Test
    void shouldReturn422WhenInvalidInputIsProvided() throws Exception {
        // Given
        AddToCartDto cartItem = AddToCartDto.builder()
                .productId(-1)
                .build();

        // When
        mockMvc.perform(post("/api/v1/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItem))
                .with(user(USERNAME)))
                .andExpect(status().isUnprocessableEntity());

        // Then
        then(cartService).shouldHaveNoInteractions();
    }

    @Test
    void shouldNotAddProductToCartWhenUserIsUnauthenticated() throws Exception {
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
    void shouldReturnAllCartProducts() throws Exception {
        // Given
        User user = getUser();

        CartItemDto cartItem = CartItemDto.builder()
                .id(1)
                .productId(1)
                .quantity(2)
                .build();

        CartDto cart = CartDto.builder()
                .cartItems(List.of(cartItem))
                .totalCost(33.5)
                .build();

        given(cartService.getAllCartProducts(user.getId())).willReturn(List.of(cart));

        // When
        MvcResult result = mockMvc.perform(get("/api/v1/cart")
                .with(user(user)))
                .andExpect(jsonPath("$[0].cartItems.size()", Matchers.is(1)))
                .andExpect(status().isOk()).andReturn();

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

    @Test
    void shouldBusinessLogicToUpdateCartItem() throws Exception {
        // Given
        User user = getUser();
        AddToCartDto cartItem = AddToCartDto.builder()
                .productId(1)
                .quantity(3)
                .build();

        // When
        mockMvc.perform(put("/api/v1/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItem))
                .with(user(user)))
                .andExpect(status().isNoContent());

        // Then
        then(cartService).should().updateCartItem(cartItem, user.getId());
    }

    @Test
    void shouldFailCartUpdateWhenUserIsUnauthenticated() throws Exception {
        // Given

        // When
        mockMvc.perform(put("/api/v1/cart"))
                .andExpect(status().isUnauthorized());

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