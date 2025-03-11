package com.cptkagan.ecommerce.controllers;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptkagan.ecommerce.DTOs.requestDTO.CartRequest;
import com.cptkagan.ecommerce.DTOs.requestDTO.OrderRequest;
import com.cptkagan.ecommerce.DTOs.responseDTO.CartResponse;
import com.cptkagan.ecommerce.DTOs.responseDTO.OrderHistory;
import com.cptkagan.ecommerce.DTOs.responseDTO.ProductResponse;
import com.cptkagan.ecommerce.models.Cart;
import com.cptkagan.ecommerce.services.BuyerService;
import com.cptkagan.ecommerce.services.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/buyer")
@PreAuthorize("hasRole('ROLE_BUYER')") // WHY DOES IT WORK? IT MAKES IT ROLE_ROLE_BUYER???
                                       // IT WORKS CUZ SECURITY AND PREAUTHORIZE WORKS DIFFERENTLY.
                                       // IT CHECKS IF THE ROLE HAS "ROLE_" IN IT. ????
public class BuyerController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BuyerService buyerService;

    private ResponseEntity<?> handleBindingErrors(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }
        return null;
    }

    @Operation(summary = "Place order for items that are in the cart",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Order placement successful",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "String", example = "Order placed successfully")
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Order placement failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "User not found"),
                                @ExampleObject(name = "EmptyCart", value = "Cart is empty"),
                                @ExampleObject(name = "InsufficientStock", value = "Stock is not enough for {product_name}"),
                                @ExampleObject(name = "StripeError", value = "Stripe payment error: invalid card token")
                            }
                        )
                    )
                }
    )
    @PostMapping("/placeorder")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequest orderRequest, BindingResult bindingResult, Authentication authentication) {
        ResponseEntity<?> errorResponse = handleBindingErrors(bindingResult);
        if(errorResponse != null){
            return errorResponse;
        }

        return orderService.placeOrder(orderRequest, authentication);
    }

    @Operation(summary = "Order history",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Order history retrieved successfully",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = OrderHistory.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Order history could not be retrieved",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "User not found!")
                        )
                    )
                }
    )
    @GetMapping("/orderhistory")
    public ResponseEntity<?> getOrderHistory(Authentication authentication) {
        return buyerService.getOrderHistory(authentication);
    }
    
    @Operation(summary = "Cancel single order item",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Order item cancelled successfully",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Order item cancelled successfully!")
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Order item cancellation failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "User not found"),
                                @ExampleObject(name = "ItemNotFound", value = "Item not found!"),
                                @ExampleObject(name = "NotBelongToUser", value = "Item does not belong to the user!"),
                                @ExampleObject(name = "NotSuitableForCancellation", value = "Item is already shipped or delivered, cannot be cancelled!")
                            }
                        )
                    )
                }
    )
    @PostMapping("/cancelorderitem/{id}")
    public ResponseEntity<?> cancelOrderItem(@PathVariable Long id, Authentication authentication) {
        return buyerService.cancelOrderItem(id, authentication);
    }
    
    @Operation(summary = "Cancel order",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Order cancelled successfully",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Order cancelled succesfully. All items that are not already SHIPPED OR DELIVERED are cancelled!")
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Order cancellation failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "User not found"),
                                @ExampleObject(name = "OrderNotFound", value = "Order not found!"),
                                @ExampleObject(name = "NotBelongToUser", value = "Order does not belong to the user!"),
                                @ExampleObject(name = "NotSuitableForCancellation", value = "Order is already shipped or delivered, cannot be cancelled!")
                            }
                        )
                    )
                }
    )
    @PostMapping("/cancelorder/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, Authentication authentication){
        return buyerService.cancelOrder(id, authentication);
    }

    @Operation(summary = "Add product to cart",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "ProductQuantity", value = "Product quantity updated in cart!"),
                                @ExampleObject(name = "ProductAdded", value = "Product added to cart successfully!")
                            }
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "User not found"),
                                @ExampleObject(name = "ProductNotFound", value = "Product not found!"),
                                @ExampleObject(name = "QuantityNotPositive", value = "Quantity must be greated than 0!"),
                            }
                        )
                    )
                }
    )
    @PostMapping("/cart/addproduct")
    public ResponseEntity<?> addProductToCart(@Valid @RequestBody CartRequest cartRequest, Authentication authentication, BindingResult bindingResult) {
        ResponseEntity<?> errorResponse = handleBindingErrors(bindingResult);
        if(errorResponse != null){
            return errorResponse;
        }
        return buyerService.addProductToCart(cartRequest, authentication);
    }

    @Operation(summary = "View Cart",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = CartResponse.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "User not found!")
                        )
                    )
                }
    )
    @GetMapping("/cart")
    public ResponseEntity<?> getCart(Authentication authentication) {
        return buyerService.getCart(authentication);
    }
    
    @Operation(summary = "Update product in the cart",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Cart updated Successfully!")
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "User not found!"),
                                @ExampleObject(name = "CartItemNotFound", value = "Cart item not found!"),
                                @ExampleObject(name = "NotBelongToUser", value = "Cart item does not belong to the user!"),
                                @ExampleObject(name = "QuantityNotPositive", value = "Quantity must be greated than 0!")
                            }
                        )
                    )
                }
    )
    @PutMapping("cart/{id}/{quantity}")
    public ResponseEntity<?> updateCart(@PathVariable Long id, Authentication authentication, @PathVariable Integer quantity) {
        return buyerService.updateCart(id, authentication, quantity);
    }

    @Operation(summary = "Delete product in the cart",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Cart item deleted successfully!")
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "User not found!"),
                                @ExampleObject(name = "CartItemNotFound", value = "Cart item not found!"),
                                @ExampleObject(name = "NotBelongToUser", value = "Cart item does not belong to the user!")
                            }
                        )
                    )
                }
    )
    @DeleteMapping("cart/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable Long id, Authentication authentication){
        return buyerService.deleteCart(id, authentication);
    }

    @Operation(summary = "Add product to wishlist",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Product added to wishlist successfully!")
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "User not found!"),
                                @ExampleObject(name = "ProductNotFound", value = "Product not found!"),
                                @ExampleObject(name = "AlreadyInWishlist", value = "Product already in wishlist!")
                            }
                        )
                    )
                }
    )
    @PostMapping("/wishlist/addproduct/{id}")
    public ResponseEntity<?> addProducttoWishlsit(@PathVariable Long id, Authentication authentication){
        return buyerService.addProducttoWishlist(id, authentication);
    }

    @Operation(summary = "View wishlist",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = ProductResponse.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "User not found!")
                            }
                        )
                    )
                }
    )
    @GetMapping("/wishlist")
    public ResponseEntity<?> getWishlist(Authentication authentication){
        return buyerService.getWishlist(authentication);
    }

    @Operation(summary = "Delete item from wishlist",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Wishlist item deleted successfully!")
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "User not found!"),
                                @ExampleObject(name = "WishlistItemNotFound", value = "Wishlist item not found!")
                            }
                        )
                    )
                }
    )
    @DeleteMapping("/wishlist/{id}")
    public ResponseEntity<?> deleteWishlistItem(@PathVariable Long id, Authentication authentication){
        return buyerService.deleteWishlistItem(id, authentication);
    }
}