package com.cptkagan.ecommerce.DTOs.responseDTO;

import com.cptkagan.ecommerce.models.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LowStockWarning {
    private Long id;
    private String name;
    private int stock;

    public LowStockWarning(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.stock = product.getStockQuantity();
    }
}
