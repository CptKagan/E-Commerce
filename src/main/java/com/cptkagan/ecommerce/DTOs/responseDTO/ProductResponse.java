package com.cptkagan.ecommerce.DTOs.responseDTO;

import java.time.LocalDateTime;

import com.cptkagan.ecommerce.models.Product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String sellerName;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
        this.category = product.getCategory();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
        this.sellerName = product.getSeller().getCompanyName();
    }


}
