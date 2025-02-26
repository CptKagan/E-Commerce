package com.cptkagan.ecommerce.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.requestDTO.BuyerRegisterRequest;
import com.cptkagan.ecommerce.DTOs.responseDTO.OrderHistory;
import com.cptkagan.ecommerce.enums.OrderStatus;
import com.cptkagan.ecommerce.models.Buyer;
import com.cptkagan.ecommerce.models.Order;
import com.cptkagan.ecommerce.models.OrderItem;
import com.cptkagan.ecommerce.repositories.BuyerRepository;
import com.cptkagan.ecommerce.repositories.OrderItemRepository;
import com.cptkagan.ecommerce.repositories.OrderRepository;

@Service
public class BuyerService {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;


    public ResponseEntity<?> registerBuyer(BuyerRegisterRequest buyerRegisterRequest) {
        if(buyerRepository.existsByUserName(buyerRegisterRequest.getUserName())){
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        
        if(buyerRepository.existsByEmail(buyerRegisterRequest.getEmail())){
            return ResponseEntity.badRequest().body("Email is already taken");
        }

        Buyer buyer = new Buyer(buyerRegisterRequest, passwordEncoder.encode(buyerRegisterRequest.getPassword()));
        buyerRepository.save(buyer);
        return ResponseEntity.ok("User registered successfully");
    }

    public Buyer findByUserName (String userName){
        Optional<Buyer> buyer = buyerRepository.findByUserName(userName);
        if(buyer.isPresent()){
            return buyer.get();
        }
        return null;
    }

    public ResponseEntity<?> getOrderHistory(Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found!");
        }
        List<Order> orders = orderRepository.findByBuyerId(buyer.getId());

        List<OrderHistory> orderHistory = orders.stream().map(OrderHistory::new).collect(Collectors.toList());

        return ResponseEntity.ok(orderHistory);
    }

    public ResponseEntity<?> cancelOrderItem(Long id, Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("Usernot found!");
        }

        Optional<OrderItem> orderItemOpt = orderItemRepository.findById(id);
        if(orderItemOpt.isEmpty()){
            return ResponseEntity.badRequest().body("Item not found!");
        }

        OrderItem orderItem = orderItemOpt.get();

        if(!orderItem.getOrder().getBuyer().getId().equals(buyer.getId())){
            return ResponseEntity.badRequest().body("Item does not belong to the user!");
        }

        if(orderItem.getStatus().equals(OrderStatus.SHIPPED) || orderItem.getStatus().equals(OrderStatus.DELIVERED)){
            return ResponseEntity.badRequest().body("Item is already shipped or delivered, cannot be cancelled!");
        }

        orderItem.setStatus(OrderStatus.CANCELED);
        orderItemRepository.save(orderItem);
        return ResponseEntity.ok("Order item cancelled successfully!");
    }

    public ResponseEntity<?> cancelOrder(Long id, Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found!");
        }

        Optional<Order> orderOpt = orderRepository.findById(id);
        if(orderOpt.isEmpty()){
            return ResponseEntity.badRequest().body("Order not found!");
        }

        Order order = orderOpt.get();

        if(!order.getBuyer().getId().equals(buyer.getId())){
            return ResponseEntity.badRequest().body("Order does not belong to the user!");
        }

        if(order.getStatus().equals(OrderStatus.SHIPPED) || order.getStatus().equals(OrderStatus.DELIVERED)){
            return ResponseEntity.badRequest().body("Order is already shipped or delivered, cannot be cancelled!");
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        for(int i=0; i<order.getOrderItems().size(); i++){
            if(!order.getOrderItems().get(i).getStatus().equals(OrderStatus.SHIPPED) && !order.getOrderItems().get(i).getStatus().equals(OrderStatus.DELIVERED)){
                order.getOrderItems().get(i).setStatus(OrderStatus.CANCELED);
                orderItemRepository.save(order.getOrderItems().get(i));
            }
        }

        return ResponseEntity.ok("Order cancelled succesfully. All items that are not already SHIPPED OR DELIVERED are cancelled!");
    }
}
