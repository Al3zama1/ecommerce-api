package com.example.ecommerce.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "_order")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @Column(name = "total_price", nullable = false)
    private double totalPrice;
    @ManyToOne()
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private User user;
}
