package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.dto.product.ProductDto;
import com.example.ecommerce.api.service.interfaces.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @PostMapping()
    public ResponseEntity<Void> createProduct(@Valid @RequestBody ProductDto productDto) {
        long productId = productService.createProduct(productDto);
        return ResponseEntity.created(URI.create("/api/v1/products/" + productId)).build();
    }


}
