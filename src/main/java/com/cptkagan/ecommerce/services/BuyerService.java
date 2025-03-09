package com.cptkagan.ecommerce.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.requestDTO.BuyerRegisterRequest;
import com.cptkagan.ecommerce.DTOs.requestDTO.CartRequest;
import com.cptkagan.ecommerce.DTOs.responseDTO.CartResponse;
import com.cptkagan.ecommerce.DTOs.responseDTO.OrderHistory;
import com.cptkagan.ecommerce.DTOs.responseDTO.ProductResponse;
import com.cptkagan.ecommerce.enums.OrderStatus;
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
        if(buyerRepository.existsByUserName(buyerRegisterRequest.getUserName())){
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        
        if(buyerRepository.existsByEmail(buyerRegisterRequest.getEmail())){
            return ResponseEntity.badRequest().body("Email is already taken");
        }

        Buyer buyer = new Buyer(buyerRegisterRequest, passwordEncoder.encode(buyerRegisterRequest.getPassword()));
        buyerRepository.save(buyer);

        String verificationToken = UUID.randomUUID().toString();

        emailVerificationService.saveBuyerToken(verificationToken, buyer);

        emailVerificationService.sendVerificationEmail(buyerRegisterRequest.getEmail(), verificationToken);

        return ResponseEntity.ok("User registered successfully, please check your email to verify your account!");
    }

    public Buyer findByUserName (String userName){
        Optional<Buyer> buyer = buyerRepository.findByUserName(userName);
        if(buyer.isPresent()){
            return buyer.get();
        }
        return null;
    }

    public ResponseEntity<?> getOrderHistory(Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found!");
        }
        List<Order> orders = orderRepository.findByBuyerId(buyer.getId());

        List<OrderHistory> orderHistory = orders.stream().map(OrderHistory::new).collect(Collectors.toList());

        return ResponseEntity.ok(orderHistory);
    }

    public ResponseEntity<?> cancelOrderItem(Long id, Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("Usernot found!");
        }

        Optional<OrderItem> orderItemOpt = orderItemRepository.findById(id);
        if(orderItemOpt.isEmpty()){
            return ResponseEntity.badRequest().body("Item not found!");
        }

        OrderItem orderItem = orderItemOpt.get();

        if(!orderItem.getOrder().getBuyer().getId().equals(buyer.getId())){
            return ResponseEntity.badRequest().body("Item does not belong to the user!");
        }

        if(orderItem.getStatus().equals(OrderStatus.SHIPPED) || orderItem.getStatus().equals(OrderStatus.DELIVERED)){
            return ResponseEntity.badRequest().body("Item is already shipped or delivered, cannot be cancelled!");
        }

        orderItem.setStatus(OrderStatus.CANCELED);
        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItemRepository.save(orderItem);
        return ResponseEntity.ok("Order item cancelled successfully!");
    }

    public ResponseEntity<?> cancelOrder(Long id, Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found!");
        }

        Optional<Order> orderOpt = orderRepository.findById(id);
        if(orderOpt.isEmpty()){
            return ResponseEntity.badRequest().body("Order not found!");
        }

        Order order = orderOpt.get();

        if(!order.getBuyer().getId().equals(buyer.getId())){
            return ResponseEntity.badRequest().body("Order does not belong to the user!");
        }

        if(order.getStatus().equals(OrderStatus.SHIPPED) || order.getStatus().equals(OrderStatus.DELIVERED)){
            return ResponseEntity.badRequest().body("Order is already shipped or delivered, cannot be cancelled!");
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        for(int i=0; i<order.getOrderItems().size(); i++){
            if(!order.getOrderItems().get(i).getStatus().equals(OrderStatus.SHIPPED) && !order.getOrderItems().get(i).getStatus().equals(OrderStatus.DELIVERED)){
                order.getOrderItems().get(i).setStatus(OrderStatus.CANCELED);
                order.getOrderItems().get(i).setUpdatedAt(LocalDateTime.now());
                orderItemRepository.save(order.getOrderItems().get(i));
            }
        }

        return ResponseEntity.ok("Order cancelled succesfully. All items that are not already SHIPPED OR DELIVERED are cancelled!");
    }

    public ResponseEntity<?> addProductToCart(CartRequest cartRequest, Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found!");
        }

        Optional<Product> productOpt = productRepository.findById(cartRequest.getProductId());
        if(!productOpt.isPresent()){
            return ResponseEntity.badRequest().body("Product not found!");
        }

        if(cartRequest.getQuantity() <= 0){
            return ResponseEntity.badRequest().body("Quantity must be greated than 0!");
        }

        Optional<Cart> cartOpt = cartRepository.findByBuyerIdAndProductId(buyer.getId(), cartRequest.getProductId());
        if(cartOpt.isPresent()){
            Cart cart = cartOpt.get();
            cart.setQuantity(cart.getQuantity() + cartRequest.getQuantity());
            cartRepository.save(cart);
            return ResponseEntity.ok("Product quantity updated in cart!");
        }

        Cart cart = new Cart();
        cart.setBuyer(buyer);
        cart.setProduct(productOpt.get());
        cart.setQuantity(cartRequest.getQuantity());

        cartRepository.save(cart);
        return ResponseEntity.ok("Product added to cart successfully!");
    }

    public ResponseEntity<?> getCart(Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found!");
        }

        List<Cart> cart = cartRepository.findAllByBuyerId(buyer.getId());
        if(cart.isEmpty()){
            return ResponseEntity.ok("Cart is empty!");
        }

        List<CartResponse> cartResponse = cart.stream().map(CartResponse::new).collect(Collectors.toList());
        double totalPriceofAll = 0;
        for(CartResponse c : cartResponse){
            totalPriceofAll += c.getPrice() * c.getQuantity();
        }

        Map<String, Object> response = Map.of("cartItems", cartResponse, "totalPrice", totalPriceofAll);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateCart(Long id, Authentication authentication, Integer quantity) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found!");
        }

        Optional<Cart> cartOpt = cartRepository.findById(id);
        if(!cartOpt.isPresent()){
            return ResponseEntity.badRequest().body("Cart item not found!");
        }

        Cart cart = cartOpt.get();
        if(!cart.getBuyer().getId().equals(buyer.getId())){
            return ResponseEntity.badRequest().body("Cart item does not belong to the user!");
        }

        if(quantity <= 0){
            return ResponseEntity.badRequest().body("Quantity must be greater than 0!");
        }

        cart.setQuantity(quantity);
        cartRepository.save(cart);
        return ResponseEntity.ok("Cart updated Successfully!");
    }

    public ResponseEntity<?> deleteCart(Long id, Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found!");
        }

        Optional<Cart> cartOpt = cartRepository.findById(id);
        if(!cartOpt.isPresent()){
            return ResponseEntity.badRequest().body("Cart item not found!");
        }

        Cart cart = cartOpt.get();
        if(!cart.getBuyer().getId().equals(buyer.getId())){
            return ResponseEntity.badRequest().body("Cart item does not belong to the user!");
        }

        cartRepository.delete(cart);
        return ResponseEntity.ok("Cart item deleted successfully!");
    }

    public ResponseEntity<?> addProducttoWishlist(Long id, Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found!");
        }

        Optional<Product> productOpt = productRepository.findById(id);
        if(!productOpt.isPresent()){
            return ResponseEntity.badRequest().body("Product not found!");
        }

        if(wishlistRepository.existsByBuyerIdAndProductId(buyer.getId(), id)){
            return ResponseEntity.badRequest().body("Product already in wishlist!");
        }

        Wishlist wishlist = new Wishlist(buyer, productOpt.get());
        wishlistRepository.save(wishlist);
        return ResponseEntity.ok("Product added to wishlist successfully!");

    }

    public ResponseEntity<?> getWishlist(Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found!");
        }

        List<Wishlist> wishlist = wishlistRepository.findAllByBuyerId(buyer.getId());
        if(wishlist.isEmpty()){
            return ResponseEntity.ok("Wishlist is empty!");
        }

        List<Product> products = wishlist.stream().map(Wishlist::getProduct).collect(Collectors.toList());
        List<ProductResponse> productResponses = products.stream().map(ProductResponse::new).collect(Collectors.toList());

        return ResponseEntity.ok(productResponses);
    }

    public ResponseEntity<?> deleteWishlistItem(Long id, Authentication authentication) {
        Buyer buyer = findByUserName(authentication.getName());
        if(buyer == null){
            return ResponseEntity.badRequest().body("User not found!");
        }

        Optional<Wishlist> wishlistOpt = wishlistRepository.findByBuyerIdAndProductId(buyer.getId(), id);
        if(!wishlistOpt.isPresent()){
            return ResponseEntity.badRequest().body("Wishlist item not found!");
        }

        Wishlist wishlist = wishlistOpt.get();
        if(!wishlist.getBuyer().getId().equals(buyer.getId())){
            return ResponseEntity.badRequest().body("Wishlist item does not belong to the user!");
        }

        wishlistRepository.delete(wishlist);
        return ResponseEntity.ok("Wishlist item deleted successfully!");
    }

    public ResponseEntity<?> verifyBuyer(String token) {
        Optional<Buyer> buyerOpt = emailVerificationService.verifyBuyer(token);
        if(buyerOpt.isEmpty()){
            return ResponseEntity.badRequest().body("Invalid/expired token or account is already verified!");
        }

        Buyer buyer = buyerOpt.get();
        buyerRepository.save(buyer);
        return ResponseEntity.ok("Account verified successfully!");
    }
}
