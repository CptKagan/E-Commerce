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
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private BuyerService accountService;


    @PostMapping("/registerbuyer")
    public ResponseEntity<?> register(@Valid @RequestBody BuyerRegisterRequest buyerRegisterRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                return ResponseEntity.badRequest().body(errors);
        }
        try{
            return accountService.registerBuyer(buyerRegisterRequest);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("User could not be registered");
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                return ResponseEntity.badRequest().body(errors);
        }
        try{
            authService.authenticate(loginRequest.getUserName(), loginRequest.getPassword());
            String token = jwtTokenUtil.generateToken(loginRequest.getUserName());
            return ResponseEntity.ok(token);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}