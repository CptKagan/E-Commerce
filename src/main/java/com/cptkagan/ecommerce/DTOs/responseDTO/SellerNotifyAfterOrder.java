package com.cptkagan.ecommerce.DTOs.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerNotifyAfterOrder {
    ProductResponse product;
    int quantity;
}