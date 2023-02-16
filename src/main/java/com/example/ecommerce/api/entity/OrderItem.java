package com.example.ecommerce.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "order_id"})
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class OrderItem {

//    @EmbeddedId
//    private OrderItemPK id;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
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
    @ManyToOne
    @JoinColumn(
            name = "order_id",
            referencedColumnName = "id"
    )
    private Order order;
}
