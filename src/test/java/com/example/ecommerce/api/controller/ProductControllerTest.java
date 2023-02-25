package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.config.JwtService;
import com.example.ecommerce.api.config.SecurityConfig;
import com.example.ecommerce.api.config.UserAuthenticationEntryPoint;
import com.example.ecommerce.api.config.WebSecurity;
import com.example.ecommerce.api.mapstruct.dto.product.AddProductDto;
import com.example.ecommerce.api.mapstruct.dto.product.ProductResponseDto;
import com.example.ecommerce.api.mapstruct.dto.product.UpdateProductDto;
import com.example.ecommerce.api.repository.UserRepository;
import com.example.ecommerce.api.service.interfaces.IProductService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private static PodamFactory podamFactory;

    private static final String USERNAME = "john@gmail.com";

    @BeforeAll
    static void setUp() {
        podamFactory = new PodamFactoryImpl();
    }

    // add new product tests
    @Test
    void shouldCallServiceLogicAndReturnNewProductLocation() throws Exception {
        // Given
        AddProductDto product = podamFactory.manufacturePojo(AddProductDto.class);

        // When
       mockMvc.perform(post("/api/v1/products")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(product))
               .with(user(USERNAME).roles("ADMIN")))
               .andExpect(status().isCreated())
               .andExpect(header().exists("Location"));

       // Then
        then(productService).should().createProduct(any(AddProductDto.class));
    }

    @Test
    void shouldNotCallServiceLogicWhenInvalidInputIsGiven() throws Exception {
        // Given
        AddProductDto product = new AddProductDto();

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
    void shouldReturn401WhenUserIsUnauthenticated() throws Exception {
        // Given
        AddProductDto product = podamFactory.manufacturePojo(AddProductDto.class);

        // When
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(objectMapper.writeValueAsString(product)))
                .andExpect(status().isUnauthorized());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturn403WhenUserDoesNotHavePermissionToAddBook() throws Exception{
        // Given
        AddProductDto product = podamFactory.manufacturePojo(AddProductDto.class);

        // When
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(objectMapper.writeValueAsString(product))
                        .with(user(USERNAME).roles("USER")))
                .andExpect(status().isForbidden());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    // return all products tests
    @Test
    void shouldReturnAllProducts() throws Exception {
        // Given
        ProductResponseDto product = podamFactory.manufacturePojo(ProductResponseDto.class);
        given(productService.getProducts()).willReturn(List.of(product));

        // When
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(jsonPath("$.size()", Matchers.is(1)))
                .andExpect(status().isOk());

        // Then
        then(productService).should().getProducts();
    }

    // return single product tests
    @Test
    void shouldReturnSingleProduct() throws Exception {
        // Given
        ProductResponseDto product = podamFactory.manufacturePojo(ProductResponseDto.class);

        given(productService.getProduct(product.getId())).willReturn(product);

        // When
        mockMvc.perform(get("/api/v1/products/{productId}", product.getId()))
                .andExpect(jsonPath("$.id", Matchers.is(product.getId())))
                .andExpect(jsonPath("$.name", Matchers.is(product.getName())))
                .andExpect(status().isOk());

        // Then
        then(productService).should().getProduct(product.getId());
    }

    @Test
    void shouldNotReturnProductIfInvalidProductIdGiven() throws Exception {
        // Given
        long productId = -1L;

        // When
        mockMvc.perform(get("/api/v1/products/{productId}", productId))
                .andExpect(status().isUnprocessableEntity());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    // update product tests
    @Test
    void shouldUpdateExistingProductWhenInputIsValid() throws Exception {
        // Given
        UpdateProductDto product = podamFactory.manufacturePojo(UpdateProductDto.class);

        // When
        mockMvc.perform(put("/api/v1/products/{productId}", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))
                .with(user(USERNAME).roles("MANAGER")))
                .andExpect(status().isNoContent());

        // Then
        then(productService).should().updateProduct(product.getId(), product);
    }

    @Test
    void shouldNotUpdateExistingProductWhenInputIsInvalid() throws Exception {
        // Given
        UpdateProductDto product = UpdateProductDto.builder()
                .id(1L)
                .name("Iphone11")
                .build();

        // When
        mockMvc.perform(put("/api/v1/products/{productId}", product.getId())
                .with(user(USERNAME).roles("MANAGER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isUnprocessableEntity());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    @Test
    void shouldNotUpdateProductWhenUserDoesNotHavePrivileges() throws Exception {
        // Given
        UpdateProductDto product = podamFactory.manufacturePojo(UpdateProductDto.class);

        // When
        mockMvc.perform(put("/api/v1/products/{productId}", product.getId())
                .with(user(USERNAME).roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(objectMapper.writeValueAsString(product)))
                .andExpect(status().isForbidden());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    // delete product tests
    @Test
    void shouldDeleteProductAndReturn204() throws Exception {
        // Given
        long productId = 1L;

        // When
        mockMvc.perform(delete("/api/v1/products/{productId}", productId)
                .with(user(USERNAME).roles("MANAGER")))
                .andExpect(status().isNoContent());

        // Then
        then(productService).should().removeProduct(productId);
    }

    @Test
    void shouldFailProductDeletionWhenUserIsUnauthenticatedAndReturn401() throws Exception {
        // Given
        long productId = 1L;

        // When
        mockMvc.perform(delete("/api/v1/products/{productId}", productId))
                .andExpect(status().isUnauthorized());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    @Test
    void shouldFailProductDeletionWhenProductIdIsNotValid() throws Exception {
        // Given
        long productId = -1;

        // When
        mockMvc.perform(delete("/api/v1/products/{productId}", productId)
                .with(user(USERNAME).roles("ADMIN")))
                .andExpect(status().isUnprocessableEntity());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

    @Test
    void shouldFailProductDeletionWhenUserDoesNotHavePrivilegeAndReturn403() throws Exception {
        // Given
        long productId = 1L;

        // When
        mockMvc.perform(delete("/api/v1/products/{productId}", productId)
                .with(user(USERNAME).roles("USER")))
                .andExpect(status().isForbidden());

        // Then
        then(productService).shouldHaveNoInteractions();
    }

}