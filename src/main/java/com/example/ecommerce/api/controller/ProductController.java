package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.mapstruct.dto.product.AddProductDto;
import com.example.ecommerce.api.mapstruct.dto.product.ProductResponseDto;
import com.example.ecommerce.api.mapstruct.dto.product.UpdateProductDto;
import com.example.ecommerce.api.service.interfaces.IProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final IProductService productService;

    @PostMapping
    public ResponseEntity<Void> createProduct(@Valid @RequestBody AddProductDto addProductDto) {
        long productId = productService.createProduct(addProductDto);
        return ResponseEntity.created(URI.create("/api/v1/products/" + productId)).build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponseDto> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponseDto getProduct(@PositiveOrZero @PathVariable long productId) {
        return productService.getProduct(productId);
    }

    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProduct(@PositiveOrZero @PathVariable long productId,
                                              @Valid @RequestBody UpdateProductDto product) {
        productService.updateProduct(productId, product);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProduct(@PositiveOrZero @PathVariable long productId) {
        productService.removeProduct(productId);
    }




}
