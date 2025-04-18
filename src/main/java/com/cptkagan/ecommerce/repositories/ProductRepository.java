package com.cptkagan.ecommerce.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cptkagan.ecommerce.models.Product;

import jakarta.persistence.LockModeType;

/**
 * Added JpaSpecificationExecuter to the repository to be able to use JPA Specifications. 
 * This one lets us use a Dynamic Filtering mechanism to filter the products based on the given parameters.
 * It has "Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable); [supports pagination]" method as default
 * Dynamic query -> No need to write 100s of queries for each filter possibility
 */

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsBySellerIdAndName(Long sellerId, String name);
    Optional<Product> findById(Long id);
    List<Product> findAllBySellerId(Long sellerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdWithLock(Long productId);
}
