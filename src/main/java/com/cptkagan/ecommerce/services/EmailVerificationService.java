package com.cptkagan.ecommerce.services;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.models.Buyer;
import com.cptkagan.ecommerce.models.BuyerVerification;
import com.cptkagan.ecommerce.models.Seller;
import com.cptkagan.ecommerce.models.SellerVerification;
import com.cptkagan.ecommerce.repositories.BuyerVerificationRepository;
import com.cptkagan.ecommerce.repositories.SellerVerificationRepository;

@Service
public class EmailVerificationService {
    @Autowired
    private EmailService emailService;

    @Autowired
    private BuyerVerificationRepository buyerVerificationRepository;

    @Autowired
    private SellerVerificationRepository sellerVerificationRepository;

    public void saveBuyerToken(String token, Buyer buyer){
        BuyerVerification verification = new BuyerVerification();
        verification.setBuyer(buyer);
        verification.setToken(token);
        verification.setCreatedAt(LocalDateTime.now());
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        buyerVerificationRepository.save(verification);
    }

    public void saveSellerToken(String token, Seller seller){
        SellerVerification verification = new SellerVerification();
        verification.setSeller(seller);
        verification.setToken(token);
        verification.setCreatedAt(LocalDateTime.now());
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        sellerVerificationRepository.save(verification);
    }

    public void sendVerificationEmail(String to, String token){
        emailService.sendVerificationEmail(to, token);
    }

    public Optional<Buyer> verifyBuyer(String token) {
        Optional<BuyerVerification> verificationOpt = buyerVerificationRepository.findByToken(token);
        if(!verificationOpt.isPresent()){
            return Optional.empty();
        }

        BuyerVerification verificaiton = verificationOpt.get();
        Buyer buyer = verificaiton.getBuyer();

        if(buyer.getIsActivated()){
            return Optional.empty();
        }

        if(verificaiton.getExpiresAt().isBefore(LocalDateTime.now())){
            return Optional.empty();
        }

        buyerVerificationRepository.delete(verificaiton);

        buyer.setIsActivated(true);
        return Optional.of(buyer);
    }

    public Optional<Seller> verifySeller(String token){
        Optional<SellerVerification> verificationOpt = sellerVerificationRepository.findByToken(token);
        if(!verificationOpt.isPresent()){
            return Optional.empty();
        }
        SellerVerification verification = verificationOpt.get();
        Seller seller = verification.getSeller();

        if(seller.getIsActivated()){
            return Optional.empty();
        }

        if(verification.getExpiresAt().isBefore(LocalDateTime.now())){
            return Optional.empty();
        }

        sellerVerificationRepository.delete(verification);

        seller.setIsActivated(true);
        return Optional.of(seller);
    }
}
