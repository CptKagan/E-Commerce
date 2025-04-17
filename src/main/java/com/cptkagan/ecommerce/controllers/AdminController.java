package com.cptkagan.ecommerce.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptkagan.ecommerce.DTOs.responseDTO.SellerResponseAdmin;
import com.cptkagan.ecommerce.services.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole(ROLE_ADMIN)")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "View sellers that waiting a moderator approve", responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = SellerResponseAdmin.class))),
            @ApiResponse(responseCode = "400", description = "Failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Unauthorized")))
    })
    @GetMapping("/waitingapprove")
    public ResponseEntity<?> getWaitingApprove(Authentication authentication) {
        List<SellerResponseAdmin> sellerResponseAdmin = adminService.getWaitingApprove(authentication.getName());
        if (sellerResponseAdmin == null) {
            return ResponseEntity.ok("No seller waiting for an approval!");
        }

        return ResponseEntity.ok(sellerResponseAdmin);
    }

    @Operation(summary = "Approve seller", responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Seller approved. Now login is allowed!"))),
            @ApiResponse(responseCode = "400", description = "Failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = {
                    @ExampleObject(name = "NoSellerExists", value = "No seller exists with that id!"),
                    @ExampleObject(name = "EmailNotVerified", value = "Email not verified. Is not approvable yet!"),
                    @ExampleObject(name = "AlreadyApproved", value = "Seller is already approved!"),
                    @ExampleObject(name = "AlreadyRejected", value = "Seller is already rejected. You can not approve an already rejected seller.")
            }))
    })
    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approveSeller(@PathVariable Long id, Authentication authentication) {
        SellerResponseAdmin sellerResponseAdmin = adminService.approveSeller(id, authentication.getName());
        return ResponseEntity.ok(sellerResponseAdmin);
    }

    @Operation(summary = "Reject seller", responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Seller rejected."))),
            @ApiResponse(responseCode = "400", description = "Failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = {
                    @ExampleObject(name = "NoSellerExists", value = "No seller exists with that id!"),
                    @ExampleObject(name = "EmailNotVerified", value = "Email not verified. Is not approvable yet!"),
                    @ExampleObject(name = "AlreadyApproved", value = "Seller is already approved!"),
                    @ExampleObject(name = "AlreadyRejected", value = "Seller is already rejected. You can not reject an already rejected seller.")
            }))
    })
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectSeller(@PathVariable Long id, Authentication authentication) {
        SellerResponseAdmin sellerResponseAdmin = adminService.rejectSeller(id, authentication);
        return ResponseEntity.ok(sellerResponseAdmin);
    }

    @Operation(summary = "Approve rejected seller", responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Seller approved. Now login is allowed!"))),
            @ApiResponse(responseCode = "400", description = "Failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = {
                    @ExampleObject(name = "NoSellerExists", value = "No seller exists with that id!"),
                    @ExampleObject(name = "EmailNotVerified", value = "Email not verified. Is not approvable yet!"),
                    @ExampleObject(name = "AlreadyApproved", value = "Seller is already approved!"),
                    @ExampleObject(name = "WaitingApproval", value = "User is already waiting for an approval. Its not rejected!")
            }))
    })
    @PostMapping("/approverejected/{id}")
    public ResponseEntity<?> approveRejectedSeller(@PathVariable Long id, Authentication authentication) {
        SellerResponseAdmin sellerResponseAdmin = adminService.approveRejectedSeller(id, authentication);
        return ResponseEntity.ok(sellerResponseAdmin);
    }

}
