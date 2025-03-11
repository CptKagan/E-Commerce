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

import com.cptkagan.ecommerce.DTOs.requestDTO.NewProduct;
import com.cptkagan.ecommerce.DTOs.requestDTO.UpdateProduct;
import com.cptkagan.ecommerce.DTOs.responseDTO.LowStockWarning;
import com.cptkagan.ecommerce.DTOs.responseDTO.OrdersSeller;
import com.cptkagan.ecommerce.DTOs.responseDTO.SalesReportResponse;
import com.cptkagan.ecommerce.services.SellerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('ROLE_SELLER')")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    private ResponseEntity<?> handleBindingErrors(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }
        return null;
    }

    @Operation(summary = "Add product to list",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Product added successfully")
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "Invalid token!"),
                                @ExampleObject(name = "AlreadyExists", value = "Product already exists")
                            }
                        )
                    )
                }
    )
    @PostMapping("/addproduct")
    public ResponseEntity<?> addProduct(@Valid @RequestBody NewProduct newProduct, BindingResult bindingResult, Authentication authentication) {
        ResponseEntity<?> errorResponse = handleBindingErrors(bindingResult);
        if(errorResponse != null){
            return errorResponse;
        }

        return sellerService.addProduct(newProduct, authentication);
    }

    @Operation(summary = "Update product",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Product updated successfully")
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "Invalid token!"),
                                @ExampleObject(name = "AlreadyExists", value = "Product already exists"),
                                @ExampleObject(name = "NotAuthorized", value = "You are not authorized to update this product")
                            }
                        )
                    )
                }
    )
    @PutMapping("products/{id}")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody UpdateProduct updateProduct, @PathVariable Long id,
                                Authentication authentication, BindingResult bindingResult) {
        ResponseEntity<?> errorResponse = handleBindingErrors(bindingResult);
        if(errorResponse != null){
            return errorResponse;
        }                           
        return sellerService.updateProduct(updateProduct, id, authentication);
    }

    @Operation(summary = "Delete product",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Product deleted successfully")
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "Invalid token!"),
                                @ExampleObject(name = "AlreadyExists", value = "Product already exists"),
                                @ExampleObject(name = "NotAuthorized", value = "You are not authorized to delete this product")
                            }
                        )
                    )
                }
    )
    @DeleteMapping("products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Authentication authentication) {
        return sellerService.deleteProduct(id, authentication);
    }

    @Operation(summary = "Order history",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = OrdersSeller.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "Seller not found")
                            }
                        )
                    )
                }
    )
    @GetMapping("/orderhistory")
    public ResponseEntity<?> orderHistory(Authentication authentication) {
        return sellerService.orderHistory(authentication);
    }
    
    @Operation(summary = "Update status of single order item",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Order Status updated Successfully!")
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "Seller not found"),
                                @ExampleObject(name = "ItemNotFound", value = "Order not found!"),
                                @ExampleObject(name = "Unauthorized", value = "You are not authorized to update this order!"),
                                @ExampleObject(name = "NotSuitableForUpdate", value = "Order is already delivered or cancelled, cannot be updated!"),
                                @ExampleObject(name = "AlreadySameStatus", value = "Order is already in this status!"),
                                @ExampleObject(name = "InvalidStatus", value = "Invalid status!")
                            }
                        )
                    )
                }
    )
    @PutMapping("/updatestatus/{id}/{status}") // STATUS UPDATE // PATH OLARAK ALIYORUZ, BUNUN DEĞİŞMESİ GEREKLİ
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @PathVariable int status,Authentication authentication) {
        return sellerService.updateStatus(id, status, authentication);
    }

    @Operation(summary = "View sales report",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = SalesReportResponse.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "Seller not found"),
                                @ExampleObject(name = "NoSalesFound", value = "No sales found!")
                            }
                        )
                    )
                }
    )
    @GetMapping("/salesreport")
    public ResponseEntity<?> salesReport(Authentication authentication) {
        return sellerService.salesReport(authentication);
    }
    
    @Operation(summary = "View low stock products",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = LowStockWarning.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Failed",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = {
                                @ExampleObject(name = "UserNotFound", value = "Seller not found"),
                                @ExampleObject(name = "NoProductsFound", value = "No products found!")
                            }
                        )
                    )
                }
    )
    @GetMapping("/lowstock")
    public ResponseEntity<?> lowStock(Authentication authentication) {
        return sellerService.lowStock(authentication);
    }
}