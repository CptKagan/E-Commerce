package com.cptkagan.ecommerce.services;

import java.time.LocalDateTime;

import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOrderStatusEmail(String to, Long orderId, String status, LocalDateTime time){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Order Status Update");
        message.setText("Your order with id " + orderId + " is " + status + " at time " + time);
        javaMailSender.send(message);
    }

    public void sendOrderPlacedEmail(String to, Long id) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your Order Confirmation - #" + id);
        message.setText("Your order with id " + id + " has been placed successfully. Thank you for your purchase!");
        javaMailSender.send(message);
    }

    public void sendOrderItemNotifyEmail(String to, String name, int quantity, double d) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("New Order Received");
        message.setText("A new order item has been added! " + ".\n" +
            "Product: " + name + "\n" +
            "Quantity: " + quantity + "\n" +
            "Price: " + d);
        javaMailSender.send(message);
    }

    public void sendLowStockEmail(String to, String name, Integer stockQuantity) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Low Stock Alert on Product: " + name);
        message.setText("The product " + name + " has only " + stockQuantity + " left in stock. Please restock soon!");
        javaMailSender.send(message);
    }

    public void sendDiscountEmail(String to, String name, double oldPrice, Double price, Long id) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Price Drop Alert on Product: " + name);
        message.setText("The price of the product " + name + " has dropped from " + oldPrice + " to " + price + ".\n" +
            "Product ID: " + id);
        javaMailSender.send(message);
    }

    public void sendNewStockEmail(String to, String name, Integer stockQuantity, Long id) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("New Stock Alert on Product: " + name);
        message.setText("The product " + name + "with id: " + id + " has been restocked. New stock quantity: " + stockQuantity);
        javaMailSender.send(message);
    }
}
