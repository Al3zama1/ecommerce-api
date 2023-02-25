package com.example.ecommerce.api.service;

import com.example.ecommerce.api.mapstruct.dto.product.AddProductDto;
import com.example.ecommerce.api.mapstruct.dto.product.ProductResponseDto;
import com.example.ecommerce.api.mapstruct.dto.product.UpdateProductDto;
import com.example.ecommerce.api.entity.Product;
import com.example.ecommerce.api.exception.ExceptionMessages;
import com.example.ecommerce.api.exception.ProductNotFoundException;
import com.example.ecommerce.api.mapstruct.mappers.ProductMapper;
import com.example.ecommerce.api.repository.ProductRepository;
import com.example.ecommerce.api.service.interfaces.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public long createProduct(AddProductDto addProductDto) {
        Product product = productMapper.addProductDtoToProduct(addProductDto);
        product = productRepository.save(product);

        return product.getId();
    }

    @Override
    public List<ProductResponseDto> getProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();

        for (Product product : products) {
            productResponseDtos.add(productMapper.productToProductDto(product));
        }

        return productResponseDtos;
    }

    @Override
    public ProductResponseDto getProduct(long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) throw new ProductNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND);

        return productMapper.productToProductDto(productOptional.get());
    }

    @Override
    public void updateProduct(long productId, UpdateProductDto updateProductDto) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) throw new ProductNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND);

        Product product = productOptional.get();

        product.setId(updateProductDto.getId());
        product.setName(updateProductDto.getName());
        product.setDescription(product.getDescription());
        product.setPrice(updateProductDto.getPrice());
        product.setStockQuantity(updateProductDto.getStockQuantity());
        product.setImageUrl(updateProductDto.getImageUrl());

        productRepository.save(product);
    }

    @Override
    public void removeProduct(long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) throw new ProductNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND);

        productRepository.deleteById(productOptional.get().getId());
    }
}
