package com.cptkagan.ecommerce.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptkagan.ecommerce.models.SellerVerification;

@Repository
public interface SellerVerificationRepository extends JpaRepository<SellerVerification, Long>{
    Optional<SellerVerification> findByToken(String token);
}
