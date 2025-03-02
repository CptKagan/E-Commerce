package com.cptkagan.ecommerce.DTOs.responseDTO;

import java.time.LocalDateTime;

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public OrderItemResponse(OrderItem orderItem){
        this.id = orderItem.getId();
        this.orderId = orderItem.getOrder().getId();
        this.productResponse = new ProductResponse(orderItem.getProduct());
        this.status = orderItem.getStatus().toString();
        this.quantity = orderItem.getQuantity();
        this.createdAt = orderItem.getCreatedAt();
        this.updatedAt = orderItem.getUpdatedAt();
    }
}
