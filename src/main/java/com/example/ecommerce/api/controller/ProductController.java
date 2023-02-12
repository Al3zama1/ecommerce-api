package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.dto.product.AddProductDto;
import com.example.ecommerce.api.dto.product.ProductResponseDto;
import com.example.ecommerce.api.dto.product.UpdateProductDto;
import com.example.ecommerce.api.service.interfaces.IProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
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
    public List<ProductResponseDto> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{productId}")
    public ProductResponseDto getProduct(@PositiveOrZero @PathVariable long productId) {
        return productService.getProduct(productId);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(@PositiveOrZero @PathVariable long productId,
                                              @Valid @RequestBody UpdateProductDto product) {
        productService.updateProduct(productId, product);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeProduct(@PositiveOrZero @PathVariable long productId) {
        productService.removeProduct(productId);
        return ResponseEntity.noContent().build();
    }




}
