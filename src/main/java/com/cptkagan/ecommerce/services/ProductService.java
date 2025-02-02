package com.cptkagan.ecommerce.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.responseDTO.ProductResponse;
import com.cptkagan.ecommerce.models.Product;
import com.cptkagan.ecommerce.repositories.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    private Map<String, Object> createPaginatedResponse(Page<Product> productPage) {
        List<Product> products = productPage.getContent();

        if (products.isEmpty()) {
            return Collections.emptyMap();
        }

        List<ProductResponse> productResponses = products.stream().map(ProductResponse::new).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("products", productResponses);
        response.put("currentPage", productPage.getNumber());
        response.put("totalPages", productPage.getTotalPages());
        response.put("totalElements", productPage.getTotalElements());
        response.put("isLastPage", productPage.isLast());

        return response;
    }

    public ResponseEntity<?> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        return ResponseEntity.ok(createPaginatedResponse(productPage));
    }

    public ResponseEntity<?> getSingleProduct(Long id) {
        Optional<Product> products = productRepository.findById(id);
        if (!products.isPresent()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        return ResponseEntity.ok(new ProductResponse(products.get()));
    }

    public ResponseEntity<?> getProductsCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByCategory(category, pageable);

        return ResponseEntity.ok(createPaginatedResponse(productPage));
    }

    public ResponseEntity<?> getProductsName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByNameContaining(name, pageable);

        return ResponseEntity.ok(createPaginatedResponse(productPage));
    }
}
