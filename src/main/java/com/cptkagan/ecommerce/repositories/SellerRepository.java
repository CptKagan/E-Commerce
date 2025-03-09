package com.cptkagan.ecommerce.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cptkagan.ecommerce.models.Seller;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByEmail(String email);
    Optional<Seller> findByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);

    @Query("SELECT s FROM Seller s WHERE s.isApprovedByAdmin = false AND s.isActivated = true")
    List<Seller> findWaitingApprove();
}
