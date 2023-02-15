package com.example.ecommerce.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @Column(nullable = false)
    private long quantity;
    @ManyToOne()
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "id"
    )
    private Product product;
    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private User user;
}
