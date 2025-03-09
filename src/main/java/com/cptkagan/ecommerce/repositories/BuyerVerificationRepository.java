package com.cptkagan.ecommerce.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptkagan.ecommerce.models.BuyerVerification;

@Repository
public interface BuyerVerificationRepository extends JpaRepository<BuyerVerification, Long> {
    Optional<BuyerVerification> findByToken(String token);
}
