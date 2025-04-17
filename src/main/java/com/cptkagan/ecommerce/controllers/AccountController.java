package com.cptkagan.ecommerce.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.cptkagan.ecommerce.DTOs.requestDTO.BuyerRegisterRequest; 
import com.cptkagan.ecommerce.DTOs.requestDTO.LoginRequest;
import com.cptkagan.ecommerce.DTOs.requestDTO.SellerRegisterRequest;
import com.cptkagan.ecommerce.services.BuyerService;
import com.cptkagan.ecommerce.services.SellerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import com.cptkagan.ecommerce.services.AuthService;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AuthService authService;

    @Autowired
    private BuyerService buyerService;

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

    @Operation(summary = "Register endpoint for buyers",
               responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "User registered successfully",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type= "string", example = "User registered successfully, please check your email to verify your account!")
                        )
                        ),
                        @ApiResponse(
                            responseCode = "400",
                            description = "Registration failed due to duplicate username/email",
                            content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(type= "string", example = "Username is already taken / Email is already taken")
                            )
                            ),
               })
    @PostMapping("/register/buyer")
    public ResponseEntity<?> registerBuyer(@Valid @RequestBody BuyerRegisterRequest buyerRegisterRequest,
            BindingResult bindingResult) {
        ResponseEntity<?> errorResponse = handleBindingErrors(bindingResult);
        if(errorResponse != null){
            return errorResponse;
        }
        try {
            return buyerService.registerBuyer(buyerRegisterRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User could not be registered");
        }
    }

    @Operation(summary = "Email verification endpoint for buyers",
               responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Verification successful",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type= "string", example = "Account verified successfully!")
                        )
                        ),
                        @ApiResponse(
                            responseCode = "400",
                            description = "Verification failed",
                            content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(type= "string", example = "Invalid/expired token or account is already verified!")
                            )
                            ),
               })
    @PostMapping("/verify/buyer/{token}")
    public ResponseEntity<?> verifyBuyer(@PathVariable String token) {
        return buyerService.verifyBuyer(token);
    }

    @Operation(summary = "Email verification endpoint for sellers",
               responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Verification successful",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type= "string", example = "Account verified successfully! Please wait for your account to get reviewed and approved by a moderator.")
                        )
                        ),
                        @ApiResponse(
                            responseCode = "400",
                            description = "Verification failed",
                            content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(type= "string", example = "Invalid/expired token or account is already verified!")
                            )
                            ),
               })
    @PostMapping("/verify/seller/{token}")
    public ResponseEntity<?> verifySeller(@PathVariable String token) {
        return sellerService.verifySeller(token);
    }
    
    @Operation(summary = "Register endpoint for sellers",
               responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Seller registered successfully",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type= "string", example = "User registered successfully, please check your email to verify your account!")
                        )
                        ),
                        @ApiResponse(
                            responseCode = "400",
                            description = "Registration failed due to duplicate username/email",
                            content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(type= "string", example = "Username is already taken / Email is already taken")
                            )
                            ),
               })
    @PostMapping("/register/seller")
    public ResponseEntity<?> registerSeller(@Valid @RequestBody SellerRegisterRequest sellerRegisterRequest,
            BindingResult bindingResult) {
        ResponseEntity<?> errorResponse = handleBindingErrors(bindingResult);
        if(errorResponse != null){
            return errorResponse;
        }
        try {
            return sellerService.registerSeller(sellerRegisterRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User could not be registered");
        }
    }

    @Operation(summary = "Login endpoint for buyers, returns a JWT token",
               responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Login successful",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type= "string", example = "token")
                        )
                        ),
                        @ApiResponse(
                            responseCode = "400",
                            description = "Login failed",
                            content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(type= "string", example = "Invalid username or password / Account is not activated!")
                            )
                            ),
               })
    @PostMapping("/login/buyer")
    public ResponseEntity<?> buyerLogin(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        return handleLogin(loginRequest, bindingResult, "BUYER");
    }

    @Operation(summary = "Login endpoint for sellers, returns a JWT token",
               responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Login successful",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type= "string", example = "token")
                        )
                        ),
                        @ApiResponse(
                            responseCode = "400",
                            description = "Login failed",
                            content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(type= "string", example = "Invalid username or password / Account is not activated!")
                            )
                            ),
               })
    @PostMapping("/login/seller")
    public ResponseEntity<?> sellerLogin(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        return handleLogin(loginRequest, bindingResult, "SELLER");
    }

    private ResponseEntity<?> handleLogin(LoginRequest loginRequest, BindingResult bindingResult, String userType) {
        ResponseEntity<?> errorResponse = handleBindingErrors(bindingResult);
        if(errorResponse != null){
            return errorResponse;
        }
        try {
            String token = authService.login(loginRequest.getUserName(), loginRequest.getPassword(), userType);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(summary = "Login endpoint for admins, returns a JWT token",
               responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Login successful",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type= "string", example = "token")
                        )
                        ),
                        @ApiResponse(
                            responseCode = "400",
                            description = "Login failed",
                            content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(type= "string", example = "Invalid username or password / Account is not activated!")
                            )
                            ),
               })
    @PostMapping("/login/admin")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        return handleLogin(loginRequest, bindingResult, "ADMIN");
    }
}