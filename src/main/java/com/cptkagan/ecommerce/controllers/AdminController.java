package com.cptkagan.ecommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptkagan.ecommerce.services.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole(ROLE_ADMIN)")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/waitingapprove")
    public ResponseEntity<?> getWaitingApprove(Authentication authentication) {
        return adminService.getWaitingApprove(authentication);
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approveSeller(@PathVariable Long id, Authentication authentication) {
        return adminService.approveSeller(id, authentication);
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectSeller(@PathVariable Long id, Authentication authentication) {
        return adminService.rejectSeller(id, authentication);
    }
    
    @PostMapping("/approverejected/{id}")
    public ResponseEntity<?> approveRejectedSeller(@PathVariable Long id, Authentication authentication) {
        return adminService.approveRejectedSeller(id, authentication);
    }
    
    
}
