package com.cptkagan.ecommerce.DTOs.responseDTO;

import java.io.Serializable;

import com.cptkagan.ecommerce.models.Cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse implements Serializable {
    private static final long serialVersionUID=3L;
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
    private double totalPrice;

    public CartResponse(Cart cart){
        this.productId = cart.getProduct().getId();
        this.productName = cart.getProduct().getName();
        this.quantity = cart.getQuantity();
        this.price = cart.getProduct().getPrice();
        this.totalPrice = cart.getProduct().getPrice() * cart.getQuantity();
    }
}