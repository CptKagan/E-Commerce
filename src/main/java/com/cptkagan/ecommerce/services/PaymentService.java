package com.cptkagan.ecommerce.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.requestDTO.OrderRequest;
import com.cptkagan.ecommerce.exception.PaymentFailedException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public void stripePayment(OrderRequest orderRequest, double totalPrice){
        try{
        // Everything is fine, proceed to payment
        Stripe.apiKey = stripeSecretKey;

        // Process payment
        Map<String, Object> paymentMethodParams = new HashMap<>();
        paymentMethodParams.put("type", "card");
        paymentMethodParams.put("card", Map.of("token", orderRequest.getPayment().getPaymentToken()));

        PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (totalPrice * 100)) // IN CENTS
                .setCurrency("usd")
                .setPaymentMethod(paymentMethod.getId())
                .setConfirm(true)
                .setDescription("E-commerce Order Payment")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(
                                        PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                .build())
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        if (!intent.getStatus().equals("succeeded")) {
            throw new RuntimeException("Payment Failed! Status: " + intent.getStatus());
        }
        
    }catch(StripeException e){
        throw new PaymentFailedException("Stripe Payment Failed: " + e.getMessage());
    }
    }
}
