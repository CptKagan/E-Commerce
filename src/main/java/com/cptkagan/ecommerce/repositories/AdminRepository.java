package com.cptkagan.ecommerce.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cptkagan.ecommerce.models.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>{
        Optional<Admin> findByUserName(String userName);
}
