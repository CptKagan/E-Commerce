package com.cptkagan.ecommerce.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.requestDTO.BuyerRegisterRequest;
import com.cptkagan.ecommerce.DTOs.responseDTO.OrderHistory;
import com.cptkagan.ecommerce.models.Buyer;
import com.cptkagan.ecommerce.models.Order;
import com.cptkagan.ecommerce.repositories.BuyerRepository;
import com.cptkagan.ecommerce.repositories.OrderRepository;

@Service
public class BuyerService {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrderRepository orderRepository;


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

    public ResponseEntity<?> getOrderHistory(Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found!");
        }
        List<Order> orders = orderRepository.findByBuyerId(buyer.getId());

        List<OrderHistory> orderHistory = orders.stream().map(OrderHistory::new).collect(Collectors.toList());

        return ResponseEntity.ok(orderHistory);
    }
}
