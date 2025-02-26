package com.cptkagan.ecommerce.controllers;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptkagan.ecommerce.DTOs.requestDTO.OrderRequest;
import com.cptkagan.ecommerce.services.BuyerService;
import com.cptkagan.ecommerce.services.OrderService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/buyer")
@PreAuthorize("hasRole('ROLE_BUYER')") // WHY DOES IT WORK? IT MAKES IT ROLE_ROLE_BUYER???
                                       // IT WORKS CUZ SECURITY AND PREAUTHORIZE WORKS DIFFERENTLY.
                                       // IT CHECKS IF THE ROLE HAS "ROLE_" IN IT.
public class BuyerController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BuyerService buyerService;

    @PostMapping("/placeorder")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequest orderRequest, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }

        return orderService.placeOrder(orderRequest, authentication);
    }

    @GetMapping("/orderhistory")
    public ResponseEntity<?> getOrderHistory(Authentication authentication) {
        return buyerService.getOrderHistory(authentication);
    }
    
    @PostMapping("/cancelorderitem/{id}")
    public ResponseEntity<?> cancelOrderItem(@PathVariable Long id, Authentication authentication) {
        return buyerService.cancelOrderItem(id, authentication);
    }
    
    @PostMapping("/cancelorder/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, Authentication authentication){
        return buyerService.cancelOrder(id, authentication);
    }
}
