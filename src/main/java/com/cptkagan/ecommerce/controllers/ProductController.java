package com.cptkagan.ecommerce.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.cptkagan.ecommerce.DTOs.responseDTO.ProductResponse;
import com.cptkagan.ecommerce.services.ProductService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/products/id/{id}")
    public ResponseEntity<?> getSingleProduct(@PathVariable Long id) {
        try{
            ProductResponse response = productService.getSingleProductDto(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/products/filter")
    public ResponseEntity<?> getFilteredProducts(@RequestParam(required = false) String category,
                                                 @RequestParam(required = false) Double minPrice,
                                                 @RequestParam(required = false) Double maxPrice,
                                                 @RequestParam(required = false) Boolean inStock,
                                                 @RequestParam(required = false) Long sellerId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(defaultValue = "createdAt") String sortBy,
                                                 @RequestParam(defaultValue = "asc") String sortDirection,
                                                 @RequestParam(required = false) String name) {
        return productService.getFilteredProducts(category, minPrice, maxPrice, inStock, sellerId, page, size, sortBy, sortDirection, name);
    }
}
