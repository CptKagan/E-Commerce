package com.cptkagan.ecommerce.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.responseDTO.SellerResponseAdmin;
import com.cptkagan.ecommerce.models.Admin;
import com.cptkagan.ecommerce.models.Seller;
import com.cptkagan.ecommerce.repositories.AdminRepository;
import com.cptkagan.ecommerce.repositories.BuyerRepository;
import com.cptkagan.ecommerce.repositories.SellerRepository;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private EmailService emailService;

    public Admin findByUserName(String userName){
        Optional<Admin> adminOpt = adminRepository.findByUserName(userName);
        if(adminOpt.isPresent()){
            return adminOpt.get();
        }
        return null;
    }

    public ResponseEntity<?> getWaitingApprove(Authentication authentication) {
        List<Seller> sellerList = sellerRepository.findWaitingApprove();
        if(sellerList == null){
            return ResponseEntity.ok("No seller is waiting for an approve!");
        }

        List<SellerResponseAdmin> sellerResponseAdmins = new ArrayList<>();

        for(Seller s : sellerList){
            SellerResponseAdmin sro = new SellerResponseAdmin(s);
            sellerResponseAdmins.add(sro);
        }

        return ResponseEntity.ok(sellerResponseAdmins);

    }

    public ResponseEntity<?> approveSeller(Long id, Authentication authentication) {
        Seller seller = sellerService.findById(id);
        if(seller == null){
            return ResponseEntity.badRequest().body("No seller exists with that id!");
        }

        if(!seller.getIsActivated()){
            return ResponseEntity.badRequest().body("Email not verified. Is not approveable yet!");
        }

        if(seller.getIsApprovedByAdmin()){
            return ResponseEntity.ok("Seller is already approved!");
        }

        seller.setIsApprovedByAdmin(true);
        sellerRepository.save(seller);
        emailService.sendAccountApprovedEmail(seller.getEmail());
        
        return ResponseEntity.ok("Seller approved. Now login is allowed!");
    }
}
