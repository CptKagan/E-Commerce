package com.cptkagan.ecommerce.DTOs.requestDTO;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {

    @NotNull(message = "Payment details are required!")
    private PaymentInfo payment;

    private String address;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PaymentInfo{
        @NotBlank(message = "Token cannot be blank!")
        private String paymentToken;
    }
}
