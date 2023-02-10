package com.example.ecommerce.api.service.interfaces;

import com.example.ecommerce.api.dto.product.ProductDto;
public interface IProductService {

    long createProduct(ProductDto productDto);
}
