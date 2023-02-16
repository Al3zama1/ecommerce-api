package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.config.JwtService;
import com.example.ecommerce.api.config.SecurityConfig;
import com.example.ecommerce.api.config.UserAuthenticationEntryPoint;
import com.example.ecommerce.api.config.WebSecurity;
import com.example.ecommerce.api.dto.order.OrderItemDto;
import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.entity.UserRole;
import com.example.ecommerce.api.repository.UserRepository;
import com.example.ecommerce.api.service.interfaces.IOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({WebSecurity.class, SecurityConfig.class, JwtService.class, UserAuthenticationEntryPoint.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private IOrderService orderService;

    private static final String USERNAME = "john@gmail.com";

    // tests to get all orders
    @Test
    void shouldReturnOrdersWhenUserIsAuthenticated() throws Exception {
        // Given
        User user = User.builder()
                .role(UserRole.USER)
                .build();
        given(orderService.getAllOrders(user)).willReturn(List.of());

        // When
        mockMvc.perform(get("/api/v1/orders")
                .with(user(user)))
                .andExpect(jsonPath("$.size()", Matchers.is(0)))
                .andExpect(status().isOk());

        // Then
        then(orderService).should().getAllOrders(user);
    }

    @Test
    void shouldNotReturnOrdersWhenUserIsUnauthenticated() throws Exception {
        // Given

        // When
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());

        // Then
        then(orderService).shouldHaveNoInteractions();
    }

    // tests to create new order
    @Test
    void shouldCreateNewOrderAndReturnLocation() throws Exception {
        // Given
        User user = User.builder()
                .role(UserRole.USER)
                .build();
        given(orderService.createOrder(user)).willReturn(1L);

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/orders")
                .with(user(user)))
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        then(orderService).should().createOrder(user);
        assertThat(result.getResponse().getHeader("Location")).isEqualTo("/api/v1/orders/1");
    }

    @Test
    void shouldNotCreateOrderWhenUserIsUnauthenticated() throws Exception {
        // Given

        // When
        mockMvc.perform(post("/api/v1/orders"))
                .andExpect(status().isUnauthorized());

        // Then
        then(orderService).shouldHaveNoInteractions();
    }

    // tests to return a single order
    @Test
    void shouldReturnExistingUserOrderWhenUserIsAuthenticated() throws Exception {
        // Given
        User user = User.builder()
                .role(UserRole.USER)
                .build();
        long orderId = 1L;
        OrderItemDto orderItem = OrderItemDto.builder().build();

        given(orderService.getOrder(orderId, user)).willReturn(List.of(orderItem));

        // When
        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId)
                .with(user(user)))
                .andExpect(status().isOk());

        // Then
        then(orderService).should().getOrder(orderId, user);
    }

    @Test
    void shouldNotGetOrderWhenInvalidIdIsGiven() throws Exception {
       // Given
       long orderId = -1L;

       // When
        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId)
                .with(user(USERNAME)))
                .andExpect(status().isUnprocessableEntity());

        // Then
        then(orderService).shouldHaveNoInteractions();
    }
}