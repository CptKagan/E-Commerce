package com.cptkagan.ecommerce.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.requestDTO.BuyerRegisterRequest;
import com.cptkagan.ecommerce.DTOs.requestDTO.CartRequest;
import com.cptkagan.ecommerce.DTOs.responseDTO.CartDTO;
import com.cptkagan.ecommerce.DTOs.responseDTO.CartResponse;
import com.cptkagan.ecommerce.DTOs.responseDTO.OrderHistory;
import com.cptkagan.ecommerce.DTOs.responseDTO.OrderItemResponse;
import com.cptkagan.ecommerce.DTOs.responseDTO.ProductResponse;
import com.cptkagan.ecommerce.enums.OrderStatus;
import com.cptkagan.ecommerce.exception.ResourceNotFoundException;
import com.cptkagan.ecommerce.exception.UnauthorizedAccessException;
import com.cptkagan.ecommerce.models.Buyer;
import com.cptkagan.ecommerce.models.Cart;
import com.cptkagan.ecommerce.models.Order;
import com.cptkagan.ecommerce.models.OrderItem;
import com.cptkagan.ecommerce.models.Product;
import com.cptkagan.ecommerce.models.Wishlist;
import com.cptkagan.ecommerce.repositories.BuyerRepository;
import com.cptkagan.ecommerce.repositories.CartRepository;
import com.cptkagan.ecommerce.repositories.OrderItemRepository;
import com.cptkagan.ecommerce.repositories.OrderRepository;
import com.cptkagan.ecommerce.repositories.ProductRepository;
import com.cptkagan.ecommerce.repositories.WishlistRepository;

import jakarta.persistence.EntityExistsException;

import java.util.UUID;

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

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private EmailVerificationService emailVerificationService;

    public ResponseEntity<?> registerBuyer(BuyerRegisterRequest buyerRegisterRequest) {
        if (buyerRepository.existsByUserName(buyerRegisterRequest.getUserName())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        if (buyerRepository.existsByEmail(buyerRegisterRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken");
        }

        Buyer buyer = new Buyer(buyerRegisterRequest, passwordEncoder.encode(buyerRegisterRequest.getPassword()));
        buyerRepository.save(buyer);

        String verificationToken = UUID.randomUUID().toString();

        emailVerificationService.saveBuyerToken(verificationToken, buyer);

        emailVerificationService.sendVerificationEmail(buyerRegisterRequest.getEmail(), verificationToken);

        return ResponseEntity.ok("User registered successfully, please check your email to verify your account!");
    }

    public Buyer findByUserName(String userName) {
        Optional<Buyer> buyer = buyerRepository.findByUserName(userName);
        if (buyer.isPresent()) {
            return buyer.get();
        }
        return null;
    }

    public List<OrderHistory> getOrderHistory(String userName) {
        Buyer buyer = findByUserName(userName);
        if (buyer == null) {
            throw new UsernameNotFoundException(userName + " not found!");
        }
        List<Order> orders = orderRepository.findByBuyerId(buyer.getId());

        List<OrderHistory> orderHistory = orders.stream().map(OrderHistory::new).collect(Collectors.toList());

        return orderHistory;
    }

    public OrderItemResponse cancelOrderItem(Long id, String userName) {
        Buyer buyer = findByUserName(userName);
        if (buyer == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        Optional<OrderItem> orderItemOpt = orderItemRepository.findById(id);
        if (orderItemOpt.isEmpty()) {
            throw new ResourceNotFoundException("Order item not found!");
        }

        OrderItem orderItem = orderItemOpt.get();

        if (!orderItem.getOrder().getBuyer().getId().equals(buyer.getId())) {
            throw new UnauthorizedAccessException("Order item does not belong to the user!");
        }

        if (orderItem.getStatus().equals(OrderStatus.SHIPPED) || orderItem.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new IllegalArgumentException("Order item is already shipped or delivered, cannot be cancelled!");
            // return ResponseEntity.badRequest().body("Item is already shipped or
            // delivered, cannot be cancelled!");
        }

        orderItem.setStatus(OrderStatus.CANCELED);
        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItemRepository.save(orderItem);
        OrderItemResponse orderItemResponse = new OrderItemResponse(orderItem);
        return orderItemResponse;
    }

    public OrderHistory cancelOrder(Long id, String userName) {
        Buyer buyer = findByUserName(userName);
        if (buyer == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            throw new ResourceNotFoundException("Order not found!");
        }

        Order order = orderOpt.get();

        if (!order.getBuyer().getId().equals(buyer.getId())) {
            throw new UnauthorizedAccessException("Order does not belong to the user!");
        }

        if (order.getStatus().equals(OrderStatus.SHIPPED) || order.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new IllegalArgumentException("Order is already shipped or delivered, cannot be cancelled!");
        }

        for (OrderItem oi : order.getOrderItems()) {
            if (!oi.getStatus().equals(OrderStatus.SHIPPED) &&
                    !oi.getStatus().equals(OrderStatus.DELIVERED)) {
                oi.setStatus(OrderStatus.CANCELED);
                oi.setUpdatedAt(LocalDateTime.now());
                orderItemRepository.save(oi);
            }
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        orderOpt = orderRepository.findById(id);
        order = orderOpt.get();
        return new OrderHistory(order);
    }

    @CacheEvict(value = "cart", key = "#userName", beforeInvocation = false)
    public CartResponse addProductToCart(CartRequest cartRequest, String userName) {
        Buyer buyer = findByUserName(userName);
        if (buyer == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        Optional<Product> productOpt = productRepository.findById(cartRequest.getProductId());
        if (!productOpt.isPresent()) {
            throw new ResourceNotFoundException("Product not found!");
        }

        if (cartRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0!");
        }

        Optional<Cart> cartOpt = cartRepository.findByBuyerIdAndProductId(buyer.getId(), cartRequest.getProductId());
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cart.setQuantity(cart.getQuantity() + cartRequest.getQuantity());
            cartRepository.save(cart);
            return new CartResponse(cart);
        }

        Cart cart = new Cart();
        cart.setBuyer(buyer);
        cart.setProduct(productOpt.get());
        cart.setQuantity(cartRequest.getQuantity());

        cartRepository.save(cart);
        return new CartResponse(cart);
    }

    @Cacheable(value = "cart", key = "#userName")
    public CartDTO getCart(String userName) {
        Buyer buyer = findByUserName(userName);
        if (buyer == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        List<Cart> cart = cartRepository.findAllByBuyerId(buyer.getId());
        if (cart.isEmpty()) {
            return new CartDTO();
        }

        List<CartResponse> cartResponse = cart.stream().map(CartResponse::new).collect(Collectors.toList());
        double totalPriceofAll = 0;
        for (CartResponse c : cartResponse) {
            totalPriceofAll += c.getPrice() * c.getQuantity();
        }

        return new CartDTO(cartResponse, totalPriceofAll);
    }

    @CacheEvict(value = "cart", key = "#userName", beforeInvocation = false)
    public CartResponse updateCart(Long id, String userName, Integer quantity) {
        Buyer buyer = findByUserName(userName);
        if (buyer == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        Optional<Cart> cartOpt = cartRepository.findById(id);
        if (!cartOpt.isPresent()) {
            throw new ResourceNotFoundException("Cart item not found!");
        }

        Cart cart = cartOpt.get();
        if (!cart.getBuyer().getId().equals(buyer.getId())) {
            throw new UnauthorizedAccessException("Cart item does not belong to the user!");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0!");
        }

        cart.setQuantity(quantity);
        cartRepository.save(cart);
        return new CartResponse(cart);
    }

    @CacheEvict(value = "cart", key = "#userName", beforeInvocation = false)
    public void deleteCart(Long id, String userName) {
        Buyer buyer = findByUserName(userName);
        if (buyer == null) {
            throw new UsernameNotFoundException("User not found");
        }

        Optional<Cart> cartOpt = cartRepository.findById(id);
        if (!cartOpt.isPresent()) {
            throw new ResourceNotFoundException("Cart item not found!");
        }

        Cart cart = cartOpt.get();
        if (!cart.getBuyer().getId().equals(buyer.getId())) {
            throw new UnauthorizedAccessException("Cart item does not belong to the user!");
        }

        cartRepository.delete(cart);
    }

    @CacheEvict(value = "wishlist", key = "#userName", beforeInvocation = false)
    public ProductResponse addProducttoWishlist(Long id, String userName) {
        Buyer buyer = findByUserName(userName);
        if (buyer == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        Optional<Product> productOpt = productRepository.findById(id);
        if (!productOpt.isPresent()) {
            throw new ResourceNotFoundException("Product not found!");
        }

        if (wishlistRepository.existsByBuyerIdAndProductId(buyer.getId(), id)) {
            throw new EntityExistsException("Product already in wishlist!");
        }

        Wishlist wishlist = new Wishlist(buyer, productOpt.get());
        wishlistRepository.save(wishlist);
        return new ProductResponse(wishlist.getProduct());
    }

    @Cacheable(value = "wishlist", key = "#userName")
    public List<ProductResponse> getWishlist(String userName) {
        Buyer buyer = findByUserName(userName);
        if (buyer == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        List<Wishlist> wishlist = wishlistRepository.findAllByBuyerId(buyer.getId());
        if (wishlist.isEmpty()) {
            return List.of();
        }

        List<Product> products = wishlist.stream().map(Wishlist::getProduct).collect(Collectors.toList());
        List<ProductResponse> productResponses = products.stream().map(ProductResponse::new)
                .collect(Collectors.toList());

        return productResponses;
    }

    @CacheEvict(value = "wishlist", key = "#userName", beforeInvocation = false)
    public void deleteWishlistItem(Long id, String userName) {
        Buyer buyer = findByUserName(userName);
        if (buyer == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        Optional<Wishlist> wishlistOpt = wishlistRepository.findByBuyerIdAndProductId(buyer.getId(), id);
        if (!wishlistOpt.isPresent()) {
            throw new ResourceNotFoundException("Wishlist item not found!");
        }

        Wishlist wishlist = wishlistOpt.get();
        if (!wishlist.getBuyer().getId().equals(buyer.getId())) {
            throw new UnauthorizedAccessException("Wishlist item does not belong to the user!");
        }

        wishlistRepository.delete(wishlist);
    }

    public ResponseEntity<?> verifyBuyer(String token) {
        Optional<Buyer> buyerOpt = emailVerificationService.verifyBuyer(token);
        if (buyerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid/expired token or account is already verified!");
        }

        Buyer buyer = buyerOpt.get();
        buyerRepository.save(buyer);
        return ResponseEntity.ok("Account verified successfully!");
    }
}
