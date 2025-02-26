package com.cptkagan.ecommerce.DTOs.responseDTO;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportResponse {
    private double totalSales;
    private int totalOrders;
    private double averageOrderValue;
    private int uniqueBuyers;
    private int totalProductsSold;
    private Map<String, Integer> bestSellingProducts;
    private Map<String, Integer> topBuyers;
    private Map<String, Double> revenueByCategory;
    private Map<String, Integer> orderByStatus;
}
