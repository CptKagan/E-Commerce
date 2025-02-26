package com.cptkagan.ecommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cptkagan.ecommerce.models.OrderItem;
import java.util.List;
import java.util.Optional;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi " + 
           "JOIN oi.product p " +
           "WHERE p.seller.id = :sellerId")
    List<OrderItem> findByProductSeller (@Param("sellerId") Long sellerId);

    Optional<OrderItem> findById(Long id);
}
