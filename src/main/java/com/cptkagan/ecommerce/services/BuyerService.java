package com.cptkagan.ecommerce.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.BuyerRegisterRequest;
import com.cptkagan.ecommerce.models.Buyer;
import com.cptkagan.ecommerce.repositories.BuyerRepository;

@Service
public class BuyerService {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    public ResponseEntity<?> registerBuyer(BuyerRegisterRequest buyerRegisterRequest) {
        if(buyerRepository.existsByUserName(buyerRegisterRequest.getUserName())){
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        
        if(buyerRepository.existsByEmail(buyerRegisterRequest.getEmail())){
            return ResponseEntity.badRequest().body("Email is already taken");
        }

        Buyer buyer = new Buyer(buyerRegisterRequest, passwordEncoder.encode(buyerRegisterRequest.getPassword()));
        buyerRepository.save(buyer);
        return ResponseEntity.ok("User registered successfully");
    }

    public Buyer findByUserName (String userName){
        Optional<Buyer> buyer = buyerRepository.findByUserName(userName);
        if(buyer.isPresent()){
            return buyer.get();
        }
        return null;
    }
}
