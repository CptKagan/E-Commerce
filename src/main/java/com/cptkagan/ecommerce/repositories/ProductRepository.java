package com.cptkagan.ecommerce.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptkagan.ecommerce.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySellerId(Long sellerId);
    boolean existsBySellerIdAndName(Long sellerId, String name);
    Optional<Product> findById(Long id);
}
