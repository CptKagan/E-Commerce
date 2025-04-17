package com.cptkagan.ecommerce.DTOs.responseDTO;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO implements Serializable{
    private static final long serialVersionUID = 2L;
    private List<CartResponse> cartItems;
    private double totalPrice;
}
