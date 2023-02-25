package com.example.ecommerce.api.mapstruct.mappers;

import com.example.ecommerce.api.mapstruct.dto.product.AddProductDto;
import com.example.ecommerce.api.mapstruct.dto.product.ProductResponseDto;
import com.example.ecommerce.api.entity.Product;
import com.example.ecommerce.api.mapstruct.dto.product.UpdateProductDto;
import org.mapstruct.Mapper;

@Mapper
public interface ProductMapper {

    ProductResponseDto productToProductDto(Product product);
    Product addProductDtoToProduct(AddProductDto addProductDto);
    Product updateProductDtoToProduct(UpdateProductDto updateProductDto);
}
