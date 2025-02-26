package com.cptkagan.ecommerce.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.requestDTO.OrderRequest;
import com.cptkagan.ecommerce.enums.OrderStatus;
import com.cptkagan.ecommerce.models.Buyer;
import com.cptkagan.ecommerce.models.Order;
import com.cptkagan.ecommerce.models.OrderItem;
import com.cptkagan.ecommerce.models.Product;
import com.cptkagan.ecommerce.repositories.OrderRepository;
import com.cptkagan.ecommerce.repositories.ProductRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BuyerService buyerService;

    // ------------------- BLOCKING MUST BE ADDED HERE ----------------------
    public ResponseEntity<?> placeOrder(OrderRequest orderRequest, Authentication authentication) {
        Buyer buyer = buyerService.findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found");
        }

        // CHECK STOCKS (IDK HOW TO BLOCK STOCK WHILE GETTING MULTIPLE REQUESTS)
        for(int i = 0; i<orderRequest.getProducts().size();i++){
            Optional<Product> productOpt = productRepository.findById(orderRequest.getProducts().get(i).getProductId());
            if(productOpt.isEmpty()){
                return ResponseEntity.badRequest().body("Product not found");
            }
            Product product = productOpt.get();
            if(product.getStockQuantity() < orderRequest.getProducts().get(i).getQuantity()){
                return ResponseEntity.badRequest().body("Not enough stock for product: " + product.getName() + ". Stock: " + product.getStockQuantity());
            }
        }

        // PLACE ORDER
        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setAddress(orderRequest.getAddress());
        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0;

        // UPDATE STOCKS, STORE ORDER ITEMS
        for(int i =0; i<orderRequest.getProducts().size(); i++){
            Product product = productRepository.findById(orderRequest.getProducts().get(i).getProductId()).get();

            product.setStockQuantity(product.getStockQuantity() - orderRequest.getProducts().get(i).getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem(order, product, orderRequest.getProducts().get(i).getQuantity());
            orderItems.add(orderItem);

            totalPrice += product.getPrice() * orderRequest.getProducts().get(i).getQuantity();
        }

        // UPDATE ORDER
        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        return ResponseEntity.ok("Order placed successfully");
    }

    public void checkOrderStatus (Order order){
        if(order.getOrderItems().isEmpty()){
            return;
        }
        OrderStatus firstStatus = order.getOrderItems().get(0).getStatus();
        for(int i = 1; i<order.getOrderItems().size()-1; i++){
            if(order.getOrderItems().get(i).getStatus() != firstStatus){
                return;
            }
        }
        order.setStatus(firstStatus);
        orderRepository.save(order);
    }
}
