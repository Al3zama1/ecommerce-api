package com.example.ecommerce.api.service;

import com.example.ecommerce.api.dto.product.AddProductDto;
import com.example.ecommerce.api.dto.product.ProductResponseDto;
import com.example.ecommerce.api.dto.product.UpdateProductDto;
import com.example.ecommerce.api.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    Product mapDtoToProduct(AddProductDto addProductDto) {
        return Product.builder()
                .name(addProductDto.getName())
                .description(addProductDto.getDescription())
                .imageUrl(addProductDto.getImageUrl())
                .stockQuantity(addProductDto.getStockQuantity())
                .price(addProductDto.getPrice())
                .build();
    }

    Product mapUpdateDtoToProduct(UpdateProductDto updateProductDto) {
        return Product.builder()
                .id(updateProductDto.getId())
                .name(updateProductDto.getName())
                .description(updateProductDto.getDescription())
                .stockQuantity(updateProductDto.getStockQuantity())
                .price(updateProductDto.getPrice())
                .build();
    }


    ProductResponseDto mapProductToDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    List<ProductResponseDto> mapProductsToDto(List<Product> products) {
        return products.stream().map(product -> ProductResponseDto.builder()
                        .id(product.getId())
                        .imageUrl(product.getImageUrl())
                        .price(product.getPrice())
                        .name(product.getName())
                        .description(product.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
