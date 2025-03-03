package com.cptkagan.ecommerce.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.requestDTO.OrderRequest;
import com.cptkagan.ecommerce.enums.OrderStatus;
import com.cptkagan.ecommerce.models.Buyer;
import com.cptkagan.ecommerce.models.Cart;
import com.cptkagan.ecommerce.models.Order;
import com.cptkagan.ecommerce.models.OrderItem;
import com.cptkagan.ecommerce.models.Product;
import com.cptkagan.ecommerce.models.Seller;
import com.cptkagan.ecommerce.repositories.CartRepository;
import com.cptkagan.ecommerce.repositories.OrderRepository;
import com.cptkagan.ecommerce.repositories.ProductRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReceiptService receiptService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public ResponseEntity<?> placeOrder(OrderRequest orderRequest, Authentication authentication) {
        Buyer buyer = buyerService.findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found");
        }

        List<Cart> cart = cartRepository.findAllByBuyerId(buyer.getId());
        if(cart.isEmpty()){
            return ResponseEntity.badRequest().body("Cart is empty!");
        }

        double totalPrice = 0;
        // CHECK STOCKS (IDK HOW TO BLOCK STOCK WHILE GETTING MULTIPLE REQUESTS)
        for(Cart cartItem : cart){
            if(cartItem.getProduct().getStockQuantity() < cartItem.getQuantity()){
                return ResponseEntity.badRequest().body("Stock is not enough for " + cartItem.getProduct().getName());
            }
            totalPrice += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }

        // Everything is fine, proceed to payment
        try{
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
                            .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                            .build()
                    )
                    .build();

                PaymentIntent intent = PaymentIntent.create(params);

                if(!intent.getStatus().equals("succeeded")){
                    return ResponseEntity.badRequest().body("Payment Failed!");
                }
        } catch(StripeException e){
            return ResponseEntity.badRequest().body("Stripe payment error: "+ e.getMessage());
        }

        // PAYMENT SUCCESS
        // PLACE ORDER
        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setAddress(orderRequest.getAddress());
        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        List<Long> notifiedSellers = new ArrayList<>();
        // UPDATE STOCKS, STORE ORDER ITEMS
        for(Cart cartItems : cart){
            Product product = cartItems.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItems.getQuantity());
            productRepository.save(product);
            if(product.getStockQuantity() < product.getLowStockWarning()){
                emailService.sendLowStockEmail(product.getSeller().getEmail(), product.getName(), product.getStockQuantity());
            }

            OrderItem orderItem = new OrderItem(order, product, cartItems.getQuantity());
            orderItems.add(orderItem);

            // Notify Seller, this should change to all orderItems in one mail, not only the first item that appears.
            if(!notifiedSellers.contains(product.getSeller().getId())){
                emailService.sendOrderItemNotifyEmail(product.getSeller().getEmail(), product.getName(), cartItems.getQuantity(), product.getPrice()*cartItems.getQuantity());
                notifiedSellers.add(product.getSeller().getId());
            }
        }

        // UPDATE ORDER
        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        cartRepository.deleteAll(cart);

        // SEND EMAIL
        String invoicePath = receiptService.generateInvoice(order);
        emailService.sendInvoiceEmail(order.getBuyer().getEmail(), invoicePath, order.getId());

        return ResponseEntity.ok("Order placed successfully");
    }

    public boolean checkOrderStatus (Order order){
        if(order.getOrderItems().isEmpty()){
            return false;
        }
        OrderStatus firstStatus = order.getOrderItems().get(0).getStatus();
        for(int i = 1; i<order.getOrderItems().size(); i++){
            if(order.getOrderItems().get(i).getStatus() != firstStatus){
                return false;
            }
        }
        order.setStatus(firstStatus);
        orderRepository.save(order);
        return true;
    }
}
