package com.cptkagan.ecommerce.services;

import java.io.File;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

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

    public void sendVerificationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Registration Completion and Email Verification");
        message.setText("Please click the link below to verify your email address and complete your registration: " + token);
        javaMailSender.send(message);
    }

    public void sendInvoiceEmail(String to, String filePath, Long orderId){
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Your Invoice for Order #" + orderId);
            helper.setText("Dear Customer,\n\nPlease find your invoice attached.\n\nThank you fr your purchase!");

            // Attach PDF
            FileSystemResource file = new FileSystemResource(new File(filePath));
            helper.addAttachment("Invoice_Order_" + orderId + ".pdf", file);

            javaMailSender.send(message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendAccountApprovedEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your SELLER Account Has Been Approved");
        message.setText("Your account that waiting for an approval, is approved and ready to login. Thanks for choosing CptKagan E-Commerce!");
        javaMailSender.send(message);
    }
}
