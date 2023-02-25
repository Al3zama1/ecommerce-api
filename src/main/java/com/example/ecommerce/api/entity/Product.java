package com.example.ecommerce.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(name = "imageUrl")
    private String imageUrl;
    @Column(nullable = false)
    private double price;
    @Column(name = "stock_quantity", nullable = false)
    private long stockQuantity;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.price, price) == 0 && stockQuantity == product.stockQuantity &&
                Objects.equals(id, product.id) && Objects.equals(name, product.name) &&
                Objects.equals(description, product.description) && Objects.equals(imageUrl, product.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, imageUrl, price, stockQuantity);
    }
}
