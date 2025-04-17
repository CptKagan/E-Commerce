package com.cptkagan.ecommerce.DTOs.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {

    @Schema(description = "Payment info object")
    @NotNull(message = "Payment details are required!")
    private PaymentInfo payment;


    @Schema(description = "Delivery address", example = "My address 222/11", required = true)
    @NotBlank(message = "Address is required")
    private String address;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PaymentInfo{
        @Schema(description = "Payment token that received from Stripe API", example = "tok_amex", required = true)
        @NotBlank(message = "Token cannot be blank!")
        private String paymentToken;
    }
}
