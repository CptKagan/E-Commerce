package com.cptkagan.ecommerce.DTOs.responseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.cptkagan.ecommerce.models.Order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderHistory {
    private Long orderId;
    private String buyerName;
    private LocalDateTime orderDate;
    private String status;
    private String address;
    private double totalPrice;
    private List<OrderItemResponse> products;


    public OrderHistory(Order order){
        this.orderId = order.getId();
        this.buyerName = order.getBuyer().getUserName();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus().toString();
        this.address = order.getAddress();
        this.totalPrice = order.getTotalPrice();
        this.products = order.getOrderItems().stream().map(OrderItemResponse::new).collect(Collectors.toList());
    }
}
