package com.cptkagan.ecommerce.scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cptkagan.ecommerce.models.OrderItem;
import com.cptkagan.ecommerce.models.Seller;
import com.cptkagan.ecommerce.repositories.OrderItemRepository;
import com.cptkagan.ecommerce.services.EmailService;
import com.cptkagan.ecommerce.services.SellerService;

@Component
public class SellerSummaryScheduler {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SellerService sellerService;

    @Scheduled(cron = "0 59 23 * * *", zone = "Europe/Istanbul")
    public void sendDailySellerSummaries() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        List<Seller> sellers = sellerService.getAllSellers();
        if (sellers.isEmpty()) {
            return;
        }

        for (Seller seller : sellers) {
            List<OrderItem> orderItems = orderItemRepository.findTodayOrderItemsBySeller(seller.getId(), startOfDay, endOfDay);
            if(!orderItems.isEmpty()){
                double totalRevenue = 0;
                StringBuilder messageBody = new StringBuilder("Hello " + seller.getUserName() + ",\n\n");
                messageBody.append("Here is your daily summary of orders:\n");
                messageBody.append("Total Orders: " + orderItems.size() + "\n");
                messageBody.append("Order Details:\n");
                for (OrderItem orderItem : orderItems) {
                    messageBody.append("Order ID: " + orderItem.getOrder().getId() + ", ");
                    messageBody.append("Product Name: " + orderItem.getProduct().getName() + ", ");
                    messageBody.append("Quantity: " + orderItem.getQuantity() + "\n\n");
                    totalRevenue += orderItem.getProduct().getPrice() * orderItem.getQuantity();
                }
                messageBody.append("Total Revenue: $" + String.format("%.2f", totalRevenue) + "\n\n");
                messageBody.append("\nThank you for using our service!\n");

                messageBody.append("Best regards,\n");
                messageBody.append("CptKagan E-commerce Team\n");

                emailService.sendDailySummaryEmail(seller.getEmail(), messageBody.toString());
            }
        }
    }
}
