package com.cptkagan.ecommerce.DTOs.responseDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.cptkagan.ecommerce.models.Order;
import com.cptkagan.ecommerce.models.OrderItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdersSeller {
    private Long orderId;
    private String buyerFirstName;
    private String buyerLastName;
    private LocalDateTime orderDate;
    private String status;
    private String address;
    private double totalPrice;
    private List<OrderItemResponse> products;


    public OrdersSeller(Order order, Long sellerId){
        this.orderId = order.getId();
        this.buyerFirstName = order.getBuyer().getFirstName();
        this.buyerLastName = order.getBuyer().getLastName();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus().toString();
        this.address = order.getAddress();
        this.totalPrice = 0;
        // this.products = order.getOrderItems().stream().map(OrderItemResponse::new).collect(Collectors.toList());
        List<OrderItemResponse> filteredProducts = new ArrayList<>();
        for(int i = 0; i<order.getOrderItems().size();i++){
            if(order.getOrderItems().get(i).getProduct().getSeller().getId().equals(sellerId)){
                filteredProducts.add(new OrderItemResponse(order.getOrderItems().get(i)));
                this.totalPrice += order.getOrderItems().get(i).getProduct().getPrice() * order.getOrderItems().get(i).getQuantity();
            }
        }
        this.products = filteredProducts;
    }
}
