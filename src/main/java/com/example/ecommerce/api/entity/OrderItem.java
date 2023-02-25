package com.example.ecommerce.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "order_id"})
})
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private long quantity;
    @Column(nullable = false)
    private double price;
    @ManyToOne
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "id"
    )
    private Product product;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "order_id",
            referencedColumnName = "id"
    )
    private Order order;
}
