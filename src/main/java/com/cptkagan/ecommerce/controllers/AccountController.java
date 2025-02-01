package com.cptkagan.ecommerce.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.cptkagan.ecommerce.DTOs.BuyerRegisterRequest;
import com.cptkagan.ecommerce.DTOs.LoginRequest;
import com.cptkagan.ecommerce.security.JwtTokenUtil;
import com.cptkagan.ecommerce.services.BuyerService;
import com.cptkagan.ecommerce.services.AuthService;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AuthService authService;

    @Autowired
    private BuyerService buyerService;


    @PostMapping("/registerbuyer")
    public ResponseEntity<?> register(@Valid @RequestBody BuyerRegisterRequest buyerRegisterRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                return ResponseEntity.badRequest().body(errors);
        }
        try{
            return buyerService.registerBuyer(buyerRegisterRequest);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("User could not be registered");
        }
    }
    
    @PostMapping("/login/buyer")
    public ResponseEntity<?> buyerLogin(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult){
        return handleLogin(loginRequest, bindingResult, "BUYER");
    }

    @PostMapping("/login/seller")
    public ResponseEntity<?> sellerLogin(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult){
        return handleLogin(loginRequest, bindingResult, "SELLER");
    }

    private ResponseEntity<?> handleLogin(LoginRequest loginRequest, BindingResult bindingResult, String userType) {
        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                return ResponseEntity.badRequest().body(errors);
        }
        try {
            String token = authService.login(loginRequest.getUserName(), loginRequest.getPassword(), userType);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
        
    }
}