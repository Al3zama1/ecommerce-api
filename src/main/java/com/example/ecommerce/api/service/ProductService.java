package com.example.ecommerce.api.service;

import com.example.ecommerce.api.dto.product.AddProductDto;
import com.example.ecommerce.api.dto.product.ProductResponseDto;
import com.example.ecommerce.api.dto.product.UpdateProductDto;
import com.example.ecommerce.api.service.interfaces.IProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {
    @Override
    public long createProduct(AddProductDto addProductDto) {
        return 0;
    }

    @Override
    public List<ProductResponseDto> getProducts() {
        return null;
    }

    @Override
    public ProductResponseDto getProduct(long productId) {
        return null;
    }

    @Override
    public void updateProduct(long productId, UpdateProductDto product) {

    }

    @Override
    public void removeProduct(long productId) {

    }
}
