package com.example.ecommerce.api.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(unique = true, nullable = false, length = 50, updatable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(name = "imageUrl")
    private String imageUrl;
    @Column(nullable = false)
    private double price;
    @Column(name = "stock_quantity", nullable = false)
    private long stockQuantity;
}
