package com.example.ecommerce.api.service;

import com.example.ecommerce.api.mapstruct.dto.product.AddProductDto;
import com.example.ecommerce.api.mapstruct.dto.product.ProductResponseDto;
import com.example.ecommerce.api.mapstruct.dto.product.UpdateProductDto;
import com.example.ecommerce.api.entity.Product;
import com.example.ecommerce.api.exception.ExceptionMessages;
import com.example.ecommerce.api.exception.ProductNotFoundException;
import com.example.ecommerce.api.mapstruct.mappers.ProductMapper;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductService cut;
    private static ArgumentCaptor<Product> productArgumentCaptor;
    private static PodamFactory podamFactory;

    @BeforeAll
    static void setUp() {
        productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        podamFactory = new PodamFactoryImpl();
    }

    // tests to create product
    @Test
    void shouldCreateProduct() {
        // Given
        AddProductDto addProductDto = podamFactory.manufacturePojo(AddProductDto.class);

        Product product = Product.builder()
                .build();

        given(productMapper.addProductDtoToProduct(addProductDto)).willReturn(product);
        given(productRepository.save(product)).willAnswer(invocation -> {
            Product productToSave = invocation.getArgument(0);
            productToSave.setId(1L);
            return productToSave;
        });

        // When
        cut.createProduct(addProductDto);

        // Then
        then(productRepository).should().save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();
        assertThat(savedProduct.getId()).isEqualTo(1);
    }

    // tests to fetch all products
    @Test
    void shouldReturnLisOfProducts() {
        // Given
        Product product = podamFactory.manufacturePojo(Product.class);
        given(productRepository.findAll()).willReturn(List.of(product));

        // When
        cut.getProducts();

        // Then
        then(productRepository).should().findAll();
    }

    // tests to return single product
    @Test
    void shouldReturnProductByIdWhenProductExists() {
        // Given
        long productId = 1L;
        Product product = podamFactory.manufacturePojo(Product.class);
        product.setId(productId);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // When
        cut.getProduct(productId);

        // Then
        then(productRepository).should().findById(productId);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenProductIsNotFound() {
        // Given
        long productId = 1L;

        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.getProduct(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage(ExceptionMessages.PRODUCT_NOT_FOUND);

        // Then
        then(productMapper).shouldHaveNoInteractions();
    }

    // tests to update a product
    @Test
    void shouldUpdateProductWhenItExists() {
        // Given
        long productId = 1L;
        UpdateProductDto updateProductDto = podamFactory.manufacturePojo(UpdateProductDto.class);
        updateProductDto.setId(productId);



        Product product = Product.builder().build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // When
        cut.updateProduct(productId, updateProductDto);

        // Then
        then(productRepository).should().save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();
        assertThat(savedProduct.getId()).isEqualTo(updateProductDto.getId());
        assertThat(savedProduct.getName()).isEqualTo(updateProductDto.getName());
    }

    @Test
    void shouldNotUpdateProductWhenItDoesNotExist() {
        // Given
        long productId = 1L;
        UpdateProductDto updateProductDto = UpdateProductDto.builder().build();

        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.updateProduct(productId, updateProductDto))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage(ExceptionMessages.PRODUCT_NOT_FOUND);

        // Then
        then(productRepository).should(never()).save(any());
    }

    // tests to remove product
    @Test
    void shouldRemoveProductWhenItExists() {
        // Given
        long productId = 1L;
        Product product = podamFactory.manufacturePojo(Product.class);
        product.setId(productId);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // When
        cut.removeProduct(productId);

        // Then
        then(productRepository).should().deleteById(productId);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenProductToDeleteDoesNotExist() {
        // Given
        long productId = 1L;

        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.removeProduct(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage(ExceptionMessages.PRODUCT_NOT_FOUND);

        //Then
        then(productRepository).should(never()).deleteById(any());
    }

}