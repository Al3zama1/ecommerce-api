package com.example.ecommerce.api.repository;

import com.example.ecommerce.api.entity.Order;
import com.example.ecommerce.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUserOrderByDateCreatedDesc(User user);

    Optional<Order> findByUserAndId(User user, long id);


}
