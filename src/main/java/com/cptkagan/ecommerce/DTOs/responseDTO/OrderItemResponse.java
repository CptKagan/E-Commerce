package com.cptkagan.ecommerce.DTOs.responseDTO;

import com.cptkagan.ecommerce.models.OrderItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemResponse {
    private Long id;

    private Long orderId;

    private ProductResponse productResponse;

    private String status;

    private int quantity;

    public OrderItemResponse(OrderItem orderItem){
        this.id = orderItem.getId();
        this.orderId = orderItem.getId();
        this.productResponse = new ProductResponse(orderItem.getProduct());
        this.status = orderItem.getStatus().toString();
        this.quantity = orderItem.getQuantity();
    }
}
