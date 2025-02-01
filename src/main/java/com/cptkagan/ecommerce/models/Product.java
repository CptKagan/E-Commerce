package com.cptkagan.ecommerce.models;

import java.time.LocalDateTime;

import com.cptkagan.ecommerce.DTOs.NewProduct;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT", length = 500)
    private String description;

    private Double price;

    private Integer stockQuantity;

    private String category;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Seller seller;

    public Product(NewProduct dto, Seller seller) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.price = dto.getPrice();
        this.stockQuantity = dto.getStockQuantity();
        this.category = dto.getCategory();
        this.seller = seller;
    }
}
