package com.cptkagan.ecommerce.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.requestDTO.NewProduct;
import com.cptkagan.ecommerce.DTOs.requestDTO.SellerRegisterRequest;
import com.cptkagan.ecommerce.DTOs.requestDTO.UpdateProduct;
import com.cptkagan.ecommerce.models.Product;
import com.cptkagan.ecommerce.models.Seller;
import com.cptkagan.ecommerce.repositories.ProductRepository;
import com.cptkagan.ecommerce.repositories.SellerRepository;

@Service
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> registerSeller(SellerRegisterRequest sellerRegisterRequest) {
        if (sellerRepository.existsByUserName(sellerRegisterRequest.getUserName())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        if (sellerRepository.existsByEmail(sellerRegisterRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken");
        }

        Seller seller = new Seller(sellerRegisterRequest, passwordEncoder.encode(sellerRegisterRequest.getPassword()));
        sellerRepository.save(seller);
        return ResponseEntity.ok("User registered successfully");
    }

    public Seller findByUserName(String userName) {
        Optional<Seller> seller = sellerRepository.findByUserName(userName);
        if (seller.isPresent()) {
            return seller.get();
        }
        return null;
    }

    public ResponseEntity<?> addProduct(NewProduct newProduct, Authentication authentication) {
        Seller seller = findByUserName(authentication.getName());
        if (seller == null) {
            return ResponseEntity.badRequest().body("Invalid token!");
        }
        if (productRepository.existsBySellerIdAndName(seller.getId(), newProduct.getName())) {
            return ResponseEntity.badRequest().body("Product already exists");
        }
        Product product = new Product(newProduct, seller);
        product.setCreatedAt(LocalDateTime.now());
        productRepository.save(product);

        if (seller.getProducts() == null) {
            seller.setProducts(new ArrayList<>());
        }
        seller.getProducts().add(product);
        sellerRepository.save(seller);
        return ResponseEntity.ok("Product added successfully");
    }

    public ResponseEntity<?> updateProduct(UpdateProduct updateProduct, Long id, Authentication authentication) {
        Seller seller = findByUserName(authentication.getName());
        if (seller == null) {
            return ResponseEntity.badRequest().body("Invalid token!");
        }
        Optional<Product> products = productRepository.findById(id);
        if (!products.isPresent()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        Product product = products.get();
        if (!product.getSeller().getId().equals(seller.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to update this product");
        }

        if(updateProduct.getName() != null){
            product.setName(updateProduct.getName());
        }

        if(updateProduct.getPrice() != null){
            product.setPrice(updateProduct.getPrice());
        }

        if(updateProduct.getStockQuantity() != null){
            product.setStockQuantity(updateProduct.getStockQuantity());
        }

        if(updateProduct.getDescription() != null){
            product.setDescription(updateProduct.getDescription());
        }

        if(updateProduct.getCategory() != null){
            product.setCategory(updateProduct.getCategory());
        }

        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
        return ResponseEntity.ok("Product updated successfully");
    }

    public ResponseEntity<?> deleteProduct(Long id, Authentication authentication) {
        Seller seller = findByUserName(authentication.getName());
        if (seller == null) {
            return ResponseEntity.badRequest().body("Invalid token!");
        }
        Optional<Product> products = productRepository.findById(id);
        if (!products.isPresent()) {
            return ResponseEntity.badRequest().body("Product not found");
        }
        Product product = products.get();
        if(!product.getSeller().getId().equals(seller.getId())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to delete this product");
        }
        Iterator<Product> iterator = seller.getProducts().iterator();
        while(iterator.hasNext()){
            Product p = iterator.next();
            if(p.getId().equals(id)){
                iterator.remove();
                break;
            }
        }

        sellerRepository.save(seller);
        productRepository.delete(product);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
