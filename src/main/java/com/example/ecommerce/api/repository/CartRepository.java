package com.example.ecommerce.api.repository;

import com.example.ecommerce.api.entity.Cart;
import com.example.ecommerce.api.entity.Product;
import com.example.ecommerce.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findAllByUser_IdOrderByDateCreatedDesc(long userId);
    Optional<Cart> findByUserAndProduct(User user, Product product);
    List<Cart> findAllByUser(User user);
    Optional<Cart> findByIdAndUser(long id, User user);
}
