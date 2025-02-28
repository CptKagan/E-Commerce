package com.cptkagan.ecommerce.DTOs.requestDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {
    @NotNull(message = "Product id cannot be blank")
    private Long productId;

    @NotNull(message = "Quantity cannot be blank")
    private Integer quantity;
}
