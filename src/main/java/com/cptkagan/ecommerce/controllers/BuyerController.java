package com.cptkagan.ecommerce.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/buyer")
@PreAuthorize("hasRole('ROLE_BUYER')") // WHY DOES IT WORK? IT MAKES IT ROLE_ROLE_BUYER???
                                       // IT WORKS CUZ SECURITY AND PREAUTHORIZE WORKS DIFFERENTLY.
                                       // IT CHECKS IF THE ROLE HAS "ROLE_" IN IT.
public class BuyerController {

    @GetMapping("/deneme")
    public ResponseEntity<?> deneme() {
        return ResponseEntity.ok("Buyer accessed");
    }
    
}
