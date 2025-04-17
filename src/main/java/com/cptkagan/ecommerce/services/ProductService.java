package com.cptkagan.ecommerce.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.responseDTO.ProductResponse;
import com.cptkagan.ecommerce.models.Product;
import com.cptkagan.ecommerce.repositories.ProductRepository;
import com.cptkagan.ecommerce.specifications.ProductSpecification;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<?> getFilteredProducts(String category, Double minPrice,
                                                Double maxPrice, Boolean inStock,
                                                Long sellerId,int page, int size,
                                                String sortBy, String sortDirection,
                                                String name){
        Specification<Product> spec = Specification.where(ProductSpecification.hasCategory(category))
                                                   .and(ProductSpecification.hasPriceBetween(minPrice,maxPrice))
                                                   .and(ProductSpecification.isInStock(inStock))
                                                   .and(ProductSpecification.hasSeller(sellerId))
                                                   .and(ProductSpecification.hasName(name));
        
        Sort.Direction direction;
        if(sortDirection.equalsIgnoreCase("asc")){
            direction = Sort.Direction.ASC;
        }
        else{
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page,size, Sort.by(direction, sortBy));

        Page<Product> productPage = productRepository.findAll(spec,pageable);

        return ResponseEntity.ok(createPaginatedResponse(productPage));
    }



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

    // @Cacheable(value = "products", key="#id")
    // public ResponseEntity<?> getSingleProduct(Long id) {
    //     System.out.println("Fetching product with ID "+ id + " from database.");
    //     Optional<Product> products = productRepository.findById(id);
    //     if (!products.isPresent()) {
    //         return ResponseEntity.badRequest().body("Product not found");
    //     }

    //     return ResponseEntity.ok(new ProductResponse(products.get()));
    // }


    @Cacheable(value = "products", key="#id")
    public ProductResponse getSingleProductDto(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if(!productOpt.isPresent()){
            throw new RuntimeException("Product not found!");
        }
        return new ProductResponse(productOpt.get());
    }
}