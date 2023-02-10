package com.example.ecommerce.api.service.interfaces;

import com.example.ecommerce.api.dto.product.AddProductDto;
import com.example.ecommerce.api.dto.product.ProductResponseDto;

import java.util.List;

public interface IProductService {

    long createProduct(AddProductDto addProductDto);

    List<ProductResponseDto> getProducts();

    ProductResponseDto getProduct(long productId);
}
