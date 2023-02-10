package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.config.JwtService;
import com.example.ecommerce.api.config.SecurityConfig;
import com.example.ecommerce.api.config.UserAuthenticationEntryPoint;
import com.example.ecommerce.api.config.WebSecurity;
import com.example.ecommerce.api.dto.product.ProductDto;
import com.example.ecommerce.api.repository.UserRepository;
import com.example.ecommerce.api.service.interfaces.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({WebSecurity.class, SecurityConfig.class, JwtService.class, UserAuthenticationEntryPoint.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private IProductService productService;

    private static final String USERNAME = "john@gmail.com";

    @Test
    void ShouldCallServiceLogicAndReturnNewProductLocation() throws Exception {
        // Given
        ProductDto product = getProduct();

        // When
       MvcResult result = mockMvc.perform(post("/api/v1/products")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(product))
               .with(user(USERNAME).roles("ADMIN")))
               .andExpect(status().isCreated()).andReturn();

       // Then
        then(productService).should().createProduct(any(ProductDto.class));
        assertThat(result.getResponse().containsHeader("Location")).isTrue();
    }

    @Test
    void ShouldNotCallServiceLogicWhenInvalidInputIsGiven() throws Exception {
        // Given
        ProductDto product = new ProductDto();

        // When
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))
                .with(user(USERNAME).roles("MANAGER")))
                .andExpect(status().isUnprocessableEntity());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    @Test
    void ShouldReturn401WhenUserIsUnauthenticated() throws Exception {
        // Given
        ProductDto product = getProduct();

        // When
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(objectMapper.writeValueAsString(product)))
                .andExpect(status().isUnauthorized());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    @Test
    void ShouldReturn403WhenUserDoesNotHavePermissionToAddBook() throws Exception{
        // Given
        ProductDto product = getProduct();

        // When
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(objectMapper.writeValueAsString(product))
                        .with(user(USERNAME).roles("USER")))
                .andExpect(status().isForbidden());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    private ProductDto getProduct() {
        return ProductDto.builder()
                .name("Iphone 11")
                .description("This is the  newest iphone")
                .imageUrl("sfjslfjslfjl")
                .stockQuantity(11)
                .price(1100)
                .build();
    }
}