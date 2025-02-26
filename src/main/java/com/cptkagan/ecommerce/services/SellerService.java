package com.cptkagan.ecommerce.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.requestDTO.NewProduct;
import com.cptkagan.ecommerce.DTOs.requestDTO.SellerRegisterRequest;
import com.cptkagan.ecommerce.DTOs.requestDTO.UpdateProduct;
import com.cptkagan.ecommerce.DTOs.responseDTO.OrdersSeller;
import com.cptkagan.ecommerce.DTOs.responseDTO.SalesReportResponse;
import com.cptkagan.ecommerce.enums.OrderStatus;
import com.cptkagan.ecommerce.models.Order;
import com.cptkagan.ecommerce.models.OrderItem;
import com.cptkagan.ecommerce.models.Product;
import com.cptkagan.ecommerce.models.Seller;
import com.cptkagan.ecommerce.repositories.OrderItemRepository;
import com.cptkagan.ecommerce.repositories.ProductRepository;
import com.cptkagan.ecommerce.repositories.SellerRepository;

@Service
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderService orderService;

    public ResponseEntity<?> registerSeller(SellerRegisterRequest sellerRegisterRequest) {
        if (sellerRepository.existsByUserName(sellerRegisterRequest.getUserName())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        if (sellerRepository.existsByEmail(sellerRegisterRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken");
        }

        Seller seller = new Seller(sellerRegisterRequest, passwordEncoder.encode(sellerRegisterRequest.getPassword()));
        sellerRepository.save(seller);
        return ResponseEntity.ok("User registered successfully");
    }

    public Seller findByUserName(String userName) {
        Optional<Seller> seller = sellerRepository.findByUserName(userName);
        if (seller.isPresent()) {
            return seller.get();
        }
        return null;
    }

    public ResponseEntity<?> addProduct(NewProduct newProduct, Authentication authentication) {
        Seller seller = findByUserName(authentication.getName());
        if (seller == null) {
            return ResponseEntity.badRequest().body("Invalid token!");
        }
        if (productRepository.existsBySellerIdAndName(seller.getId(), newProduct.getName())) {
            return ResponseEntity.badRequest().body("Product already exists");
        }
        Product product = new Product(newProduct, seller);
        product.setCreatedAt(LocalDateTime.now());
        productRepository.save(product);

        if (seller.getProducts() == null) {
            seller.setProducts(new ArrayList<>());
        }
        seller.getProducts().add(product);
        sellerRepository.save(seller);
        return ResponseEntity.ok("Product added successfully");
    }

    public ResponseEntity<?> updateProduct(UpdateProduct updateProduct, Long id, Authentication authentication) {
        Seller seller = findByUserName(authentication.getName());
        if (seller == null) {
            return ResponseEntity.badRequest().body("Invalid token!");
        }
        Optional<Product> products = productRepository.findById(id);
        if (!products.isPresent()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        Product product = products.get();
        if (!product.getSeller().getId().equals(seller.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to update this product");
        }

        if(updateProduct.getName() != null){
            product.setName(updateProduct.getName());
        }

        if(updateProduct.getPrice() != null){
            product.setPrice(updateProduct.getPrice());
        }

        if(updateProduct.getStockQuantity() != null){
            product.setStockQuantity(updateProduct.getStockQuantity());
        }

        if(updateProduct.getDescription() != null){
            product.setDescription(updateProduct.getDescription());
        }

        if(updateProduct.getCategory() != null){
            product.setCategory(updateProduct.getCategory());
        }

        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
        return ResponseEntity.ok("Product updated successfully");
    }

    public ResponseEntity<?> deleteProduct(Long id, Authentication authentication) {
        Seller seller = findByUserName(authentication.getName());
        if (seller == null) {
            return ResponseEntity.badRequest().body("Invalid token!");
        }
        Optional<Product> products = productRepository.findById(id);
        if (!products.isPresent()) {
            return ResponseEntity.badRequest().body("Product not found");
        }
        Product product = products.get();
        if(!product.getSeller().getId().equals(seller.getId())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to delete this product");
        }
        Iterator<Product> iterator = seller.getProducts().iterator();
        while(iterator.hasNext()){
            Product p = iterator.next();
            if(p.getId().equals(id)){
                iterator.remove();
                break;
            }
        }

        sellerRepository.save(seller);
        productRepository.delete(product);
        return ResponseEntity.ok("Product deleted successfully");
    }

    public ResponseEntity<?> orderHistory(Authentication authentication) {  //A Stored Procedure might good for here, but ill stick to java based
        // Get the seller
        Optional<Seller> sellerOpt = sellerRepository.findByUserName(authentication.getName());
        if(sellerOpt.isEmpty()){
            return ResponseEntity.badRequest().body("Seller not found");
        }
        Seller seller = sellerOpt.get();

        // DONT RETURN IT, ITS NOT ORDERS, IT JUST ORDERITEMS.
        List<OrderItem> allOrdersOfSeller = orderItemRepository.findByProductSeller(seller.getId());

        // Convert into orders
        Set<Order> orderOfSeller = new HashSet<> ();

        // All unique, no multiple orders
        for(int i =0; i<allOrdersOfSeller.size(); i++){
            orderOfSeller.add(allOrdersOfSeller.get(i).getOrder());
        }
        Set<OrdersSeller> ordersSeller = orderOfSeller.stream().map(order -> new OrdersSeller(order, seller.getId())).collect(Collectors.toSet());

        return ResponseEntity.ok(ordersSeller);
    }

    public ResponseEntity<?> updateStatus(Long id, int status, Authentication authentication) { // STATUS UPDATE
        Seller seller = findByUserName(authentication.getName());
        if(seller == null){
            return ResponseEntity.badRequest().body("Seller not found!");
        }

        Optional<OrderItem> orderItemOpt = orderItemRepository.findById(id);
        if(orderItemOpt.isEmpty()){
            return ResponseEntity.badRequest().body("Order not found!");
        }

        OrderItem orderItem = orderItemOpt.get();
        if(!orderItem.getProduct().getSeller().getId().equals(seller.getId())){
            return ResponseEntity.badRequest().body("You are not authorized to update this order!");
        }

        if(orderItem.getStatus().equals(OrderStatus.DELIVERED) || orderItem.getStatus().equals(OrderStatus.CANCELED)){
            return ResponseEntity.badRequest().body("Order is already delivered or cancelled, cannot be updated!");
        }

        if(status == 1){
            orderItem.setStatus(OrderStatus.PENDING);
        }
        else if(status == 2){
            orderItem.setStatus(OrderStatus.PREPARING);
        }
        else if(status == 3){
            orderItem.setStatus(OrderStatus.SHIPPED);
        }
        else if(status == 4){
            orderItem.setStatus(OrderStatus.DELIVERED);
        }
        else if(status == 5){
            orderItem.setStatus(OrderStatus.CANCELED);
        }
        else{
            return ResponseEntity.badRequest().body("Invalid status!");
        }

        orderItemRepository.save(orderItem);

        orderService.checkOrderStatus(orderItem.getOrder());

        return ResponseEntity.ok("Order Status updated Successfully!");
    }

    public ResponseEntity<?> salesReport(Authentication authentication) {
        Seller seller = findByUserName(authentication.getName());
        if(seller == null){
            return ResponseEntity.badRequest().body("Seller not found!");
        }

        List<OrderItem> orderItems = orderItemRepository.findByProductSeller(seller.getId());
        if(orderItems.isEmpty()){
            return ResponseEntity.badRequest().body("No sales found!");
        }

        double totalSales = 0;
        int totalOrders = 0;
        double averageOrderValue = 0;
        int uniqueBuyers = 0;
        Map<String, Integer> bestSellingProducts = new HashMap<>();
        Map<String, Integer> topBuyers = new HashMap<>();
        Map<String, Double> revenueByCategory = new HashMap<>();
        Map<String, Integer> orderByStatus = new HashMap<>();
        int totalProductsSold = 0;

        for(OrderItem orderItem : orderItems){

            orderByStatus.put(orderItem.getStatus().toString(), orderByStatus.getOrDefault(orderItem.getStatus().toString(), 0) + 1);

            if(orderItem.getStatus().equals(OrderStatus.DELIVERED)){
                totalSales += orderItem.getProduct().getPrice() * orderItem.getQuantity();
                totalOrders++;
                totalProductsSold += orderItem.getQuantity();

                bestSellingProducts.put(orderItem.getProduct().getName(), bestSellingProducts.getOrDefault(orderItem.getProduct().getName(),0 ) + orderItem.getQuantity());

                revenueByCategory.put(orderItem.getProduct().getCategory(), 
                                      revenueByCategory.getOrDefault(orderItem.getProduct().getCategory(), 0.0) + orderItem.getProduct().getPrice() * orderItem.getQuantity());

                topBuyers.put(orderItem.getOrder().getBuyer().getUserName(), topBuyers.getOrDefault(orderItem.getOrder().getBuyer().getUserName(), 0) + 1);
            }
        }

        uniqueBuyers = topBuyers.size();

        if(totalOrders != 0 && totalSales != 0){
            averageOrderValue = totalSales / totalOrders;
        }

        SalesReportResponse report = new SalesReportResponse(
            totalSales,
            totalOrders,
            averageOrderValue,
            uniqueBuyers,
            totalProductsSold,
            bestSellingProducts,
            topBuyers,
            revenueByCategory,
            orderByStatus
        );

        return ResponseEntity.ok(report);
    }
}
