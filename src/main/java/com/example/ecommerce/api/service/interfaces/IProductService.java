package com.example.ecommerce.api.service.interfaces;

import com.example.ecommerce.api.dto.product.AddProductDto;
import com.example.ecommerce.api.dto.product.ProductResponseDto;
import com.example.ecommerce.api.dto.product.UpdateProductDto;

import java.util.List;

public interface IProductService {

    long createProduct(AddProductDto addProductDto);

    List<ProductResponseDto> getProducts();

    ProductResponseDto getProduct(long productId);

    void updateProduct(long productId, UpdateProductDto product);
}
