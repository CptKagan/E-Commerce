package com.cptkagan.ecommerce.controllers;

import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return productService.getProducts(page, size);
    }

    @GetMapping("/products/id/{id}")
    public ResponseEntity<?> getSingleProduct(@PathVariable Long id) {
        return productService.getSingleProduct(id);
    }
    
    @GetMapping("/products/category")
    public ResponseEntity<?> getProductsCategory(@RequestParam String category, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsCategory(category, page, size);
    }
    
    @GetMapping("/products/search")
    public ResponseEntity<?> getProductsName(@RequestParam String name, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsName(name, page, size);
    }   
}
