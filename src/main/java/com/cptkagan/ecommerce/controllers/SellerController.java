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

import com.cptkagan.ecommerce.DTOs.requestDTO.NewProduct;
import com.cptkagan.ecommerce.DTOs.requestDTO.UpdateProduct;
import com.cptkagan.ecommerce.services.SellerService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('ROLE_SELLER')")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    private ResponseEntity<?> handleBindingErrors(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }
        return null;
    }

    @PostMapping("/addproduct")
    public ResponseEntity<?> addProduct(@Valid @RequestBody NewProduct newProduct, BindingResult bindingResult, Authentication authentication) {
        ResponseEntity<?> errorResponse = handleBindingErrors(bindingResult);
        if(errorResponse != null){
            return errorResponse;
        }

        return sellerService.addProduct(newProduct, authentication);
    }

    @PutMapping("products/{id}")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody UpdateProduct updateProduct, @PathVariable Long id,
                                Authentication authentication, BindingResult bindingResult) {
        ResponseEntity<?> errorResponse = handleBindingErrors(bindingResult);
        if(errorResponse != null){
            return errorResponse;
        }                           
        return sellerService.updateProduct(updateProduct, id, authentication);
    }

    @DeleteMapping("products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Authentication authentication) {
        return sellerService.deleteProduct(id, authentication);
    }

    @GetMapping("/orderhistory")
    public ResponseEntity<?> orderHistory(Authentication authentication) {
        return sellerService.orderHistory(authentication);
    }
    
    @PutMapping("/updatestatus/{id}/{status}") // STATUS UPDATE // PATH OLARAK ALIYORUZ, BUNUN DEĞİŞMESİ GEREKLİ
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @PathVariable int status,Authentication authentication) {
        return sellerService.updateStatus(id, status, authentication);
    }

    // ZAMANA GÖRE FİLTRELEME YAPILACAK
    @GetMapping("/salesreport")
    public ResponseEntity<?> salesReport(Authentication authentication) {
        return sellerService.salesReport(authentication);
    }
    
    @GetMapping("/lowstock")
    public ResponseEntity<?> lowStock(Authentication authentication) {
        return sellerService.lowStock(authentication);
    }
}