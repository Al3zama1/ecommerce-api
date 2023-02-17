package com.example.ecommerce.api.service;

import com.example.ecommerce.api.dto.product.AddProductDto;
import com.example.ecommerce.api.dto.product.ProductResponseDto;
import com.example.ecommerce.api.dto.product.UpdateProductDto;
import com.example.ecommerce.api.entity.Product;
import com.example.ecommerce.api.exception.ExceptionMessages;
import com.example.ecommerce.api.exception.ProductNotFoundException;
import com.example.ecommerce.api.repository.ProductRepository;
import com.example.ecommerce.api.service.interfaces.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public long createProduct(AddProductDto addProductDto) {
        Product product = productMapper.mapDtoToProduct(addProductDto);
        product = productRepository.save(product);

        return product.getId();
    }

    @Override
    public List<ProductResponseDto> getProducts() {
        List<Product> products = productRepository.findAll();

        return productMapper.mapProductsToDto(products);
    }

    @Override
    public ProductResponseDto getProduct(long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) throw new ProductNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND);

        return productMapper.mapProductToDto(productOptional.get());
    }

    @Override
    public void updateProduct(long productId, UpdateProductDto product) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) throw new ProductNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND);

        Product productToUpdate = productOptional.get();

        productToUpdate.setId(product.getId());
        productToUpdate.setName(product.getName());
        productToUpdate.setDescription(productToUpdate.getDescription());
        productToUpdate.setPrice(product.getPrice());
        productToUpdate.setStockQuantity(product.getStockQuantity());
        productToUpdate.setImageUrl(product.getImageUrl());

        productRepository.save(productToUpdate);
    }

    @Override
    public void removeProduct(long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isEmpty()) throw new ProductNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND);

        productRepository.deleteById(productOptional.get().getId());
    }
}
