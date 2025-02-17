package com.cptkagan.ecommerce.DTOs.requestDTO;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {

    private List<ProductOrderItem> products;

    private String address;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProductOrderItem{
        private Long productId;
        private int quantity;
    }
}
