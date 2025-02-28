package com.cptkagan.ecommerce.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptkagan.ecommerce.models.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findById(Long id);
    List<Cart> findAllByBuyerId(Long buyerId);
    Optional<Cart> findByBuyerIdAndProductId(Long buyerId, Long productId);
}
