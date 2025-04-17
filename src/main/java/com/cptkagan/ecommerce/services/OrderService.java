package com.cptkagan.ecommerce.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.cptkagan.ecommerce.DTOs.requestDTO.OrderRequest;
import com.cptkagan.ecommerce.DTOs.responseDTO.OrderHistory;
import com.cptkagan.ecommerce.DTOs.responseDTO.ProductResponse;
import com.cptkagan.ecommerce.DTOs.responseDTO.SellerNotifyAfterOrder;
import com.cptkagan.ecommerce.enums.OrderStatus;
import com.cptkagan.ecommerce.exception.PaymentFailedException;
import com.cptkagan.ecommerce.exception.ResourceNotFoundException;
import com.cptkagan.ecommerce.models.Buyer;
import com.cptkagan.ecommerce.models.Cart;
import com.cptkagan.ecommerce.models.Order;
import com.cptkagan.ecommerce.models.OrderItem;
import com.cptkagan.ecommerce.models.Product;
import com.cptkagan.ecommerce.repositories.CartRepository;
import com.cptkagan.ecommerce.repositories.OrderRepository;
import com.cptkagan.ecommerce.repositories.ProductRepository;

import jakarta.persistence.EntityManager;

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

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EntityManager entityManager;

    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 180) // 180 secs, or SET LOCAL lock_timeout = '180s' in db
    public OrderHistory placeOrder(OrderRequest orderRequest, String userName) {
        Buyer buyer = buyerService.findByUserName(userName);
        if (buyer == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        List<Cart> cart = cartRepository.findAllByBuyerId(buyer.getId());
        if (cart.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty!");
        }

        double totalPrice = 0;
        Map<Long, Product> lockedProducts = new HashMap<>();

        // LOCK STOCK BEFORE UPDATING
        for (Cart cartItem : cart) {
            Optional<Product> productOpt = productRepository.findByIdWithLock(cartItem.getProduct().getId());

            if (!productOpt.isPresent()) {
                throw new ResourceNotFoundException("Product not found!");
            }

            Product product = productOpt.get();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Stock is not enough for " + product.getName());
            }
            totalPrice += product.getPrice() * cartItem.getQuantity();
            lockedProducts.put(product.getId(), product);
        }

        // Stripe payment
        try {
            paymentService.stripePayment(orderRequest, totalPrice);
        } catch (PaymentFailedException e) {
            throw new PaymentFailedException("Payment processing failed: " + e.getMessage());
        }

        // UPDATE STOCKS
        for(Cart cartItem : cart){
            Product product = lockedProducts.get(cartItem.getProduct().getId());

            entityManager.refresh(product);

            if(product.getStockQuantity() < cartItem.getQuantity()){
                throw new IllegalArgumentException("Stock is not enough for " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            if (product.getStockQuantity() < product.getLowStockWarning()) {
                emailService.sendLowStockEmail(product.getSeller().getEmail(), product.getName(),
                        product.getStockQuantity());
            }
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
        Map<String, List<SellerNotifyAfterOrder>> sellerEmail = new HashMap<>();
        // STORE ORDER ITEMS
        for (Cart cartItem : cart) {
            Product product = lockedProducts.get(cartItem.getProduct().getId());            

            OrderItem orderItem = new OrderItem(order, product, cartItem.getQuantity());
            orderItems.add(orderItem);

            sellerEmail.computeIfAbsent(product.getSeller().getEmail(), k -> new ArrayList<>())
                    .add(new SellerNotifyAfterOrder(
                            new ProductResponse(product),
                            orderItem.getQuantity()));
        }

        // Send Email to Sellers
        for (Map.Entry<String, List<SellerNotifyAfterOrder>> entry : sellerEmail.entrySet()) {
            String to = entry.getKey();
            List<SellerNotifyAfterOrder> items = entry.getValue();

            StringBuilder message = new StringBuilder();
            message.append("A new order has been placed. Here are the details:\n\n");
            message.append("Order ID: " + order.getId() + "\n");
            message.append("Buyer: " + order.getBuyer().getUserName() + "\n");
            message.append("Buyer Email: " + order.getBuyer().getEmail() + "\n");
            message.append("Buyer Phone Number: " + order.getBuyer().getPhoneNumber() + "\n");
            message.append("Shipping Address:" + order.getAddress() + "\n\n");
            message.append("Products:\n");

            for (SellerNotifyAfterOrder item : items) {
                message.append("Product ID: ").append(item.getProduct().getId()).append("\n");
                message.append("Product: ").append(item.getProduct().getName()).append("\n");
                message.append("Quantity: ").append(item.getQuantity()).append("\n");
                message.append("Unit Price: ").append(item.getProduct().getPrice()).append("$\n");
                message.append("Total Price: ").append(item.getProduct().getPrice() * item.getQuantity()).append("$\n");
            }

            emailService.sendOrderItemNotifyEmail(to, message.toString());
        }

        // UPDATE ORDER
        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        cartRepository.deleteAll(cart);

        // SEND EMAIL
        // String invoicePath = receiptService.generateInvoice(order);
        receiptService.generateInvoice(order).thenAccept(invoicePath -> {
            if (invoicePath != null) {
                emailService.sendInvoiceEmail(order.getBuyer().getEmail(), invoicePath, order.getId());
            } else {
                System.out.println("Invoice generation failed.");
            }
        });

        /* Transaction Rollback works. Remove the part to test */
        // try{
        // Thread.sleep(200000);
        // } catch(InterruptedException e){
        // Thread.currentThread().interrupt();
        // throw new RuntimeException("Thread interrupted: " + e.getMessage());
        // }

        return new OrderHistory(order);
    }

    public boolean checkOrderStatus(Order order) {
        if (order.getOrderItems().isEmpty()) {
            return false;
        }
        OrderStatus firstStatus = order.getOrderItems().get(0).getStatus();
        for (int i = 1; i < order.getOrderItems().size(); i++) {
            if (order.getOrderItems().get(i).getStatus() != firstStatus) {
                return false;
            }
        }
        order.setStatus(firstStatus);
        orderRepository.save(order);
        return true;
    }
}
