package com.cptkagan.ecommerce.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.DTOs.responseDTO.SellerResponseAdmin;
import com.cptkagan.ecommerce.models.Admin;
import com.cptkagan.ecommerce.models.Seller;
import com.cptkagan.ecommerce.repositories.AdminRepository;
import com.cptkagan.ecommerce.repositories.SellerRepository;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SellerRepository sellerRepository;

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

    public List<SellerResponseAdmin> getWaitingApprove(String userName) {
        List<Seller> sellerList = sellerRepository.findWaitingApprove();
        if(sellerList == null){
            return null;
        }

        List<SellerResponseAdmin> sellerResponseAdmins = new ArrayList<>();

        for(Seller s : sellerList){
            SellerResponseAdmin sro = new SellerResponseAdmin(s);
            sellerResponseAdmins.add(sro);
        }

        return sellerResponseAdmins;

    }

    public SellerResponseAdmin approveSeller(Long id, String userName) {
        Seller seller = sellerService.findById(id);
        if(seller == null){
            throw new IllegalArgumentException("No seller exists with that id!");
        }

        if(!seller.getIsActivated()){
            throw new IllegalArgumentException("Email not verified. Is not approvable yet!");
        }

        if(Boolean.TRUE.equals(seller.getIsApprovedByAdmin())){
            throw new IllegalArgumentException("Seller is already approved!");
        }

        if(Boolean.FALSE.equals(seller.getIsApprovedByAdmin())){
            throw new IllegalArgumentException("Seller is already rejected. You can not approve an already rejected seller.");
        }

        seller.setIsApprovedByAdmin(true);
        sellerRepository.save(seller);
        emailService.sendAccountApprovedEmail(seller.getEmail());

        SellerResponseAdmin sellerResponseAdmin = new SellerResponseAdmin(seller);
        
        return sellerResponseAdmin;
    }

    public SellerResponseAdmin rejectSeller(Long id, Authentication authentication) {
        Seller seller = sellerService.findById(id);
        if(seller == null){
            throw new IllegalArgumentException("No seller exists with that id!");
        }

        if(!seller.getIsActivated()){
            throw new IllegalArgumentException("Email not verified. Is not approvable yet!");
        }

        if(Boolean.TRUE.equals(seller.getIsApprovedByAdmin())){
            throw new IllegalArgumentException("Seller is already approved!");
        }

        if(Boolean.FALSE.equals(seller.getIsApprovedByAdmin())){
            throw new IllegalArgumentException("Seller is already rejected. You can not approve an already rejected seller.");
        }

        seller.setIsApprovedByAdmin(false);
        sellerRepository.save(seller);
        SellerResponseAdmin sellerResponseAdmin = new SellerResponseAdmin(seller);

        return sellerResponseAdmin;
    }

    public SellerResponseAdmin approveRejectedSeller(Long id, Authentication authentication) {
        Seller seller = sellerService.findById(id);
        if(seller == null){
            throw new IllegalArgumentException("No seller exists with that id!");
        }

        if(!seller.getIsActivated()){
            throw new IllegalArgumentException("Email not verified. Is not approvable yet!");
        }

        if(Boolean.TRUE.equals(seller.getIsApprovedByAdmin())){
            throw new IllegalArgumentException("Seller is already approved!");
        }

        if(seller.getIsApprovedByAdmin() == null){
            throw new IllegalArgumentException("User is already waiting for an approval. Its not rejected!");
        }

        seller.setIsApprovedByAdmin(true);
        sellerRepository.save(seller);

        emailService.sendAccountApprovedEmail(seller.getEmail());
        SellerResponseAdmin sellerResponseAdmin = new SellerResponseAdmin(seller);
        
        return sellerResponseAdmin;
    }
}
