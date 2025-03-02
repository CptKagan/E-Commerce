package com.cptkagan.ecommerce.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptkagan.ecommerce.models.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long>{
    List<Wishlist> findAllByBuyerId(Long buyerId);
    List<Wishlist> findByProductId(Long productId);
    Optional<Wishlist> findByBuyerIdAndProductId(Long buyerId, Long productId);
    boolean existsByBuyerIdAndProductId(Long buyerId, Long productId);
}
