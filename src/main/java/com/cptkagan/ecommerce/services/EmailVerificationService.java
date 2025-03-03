package com.cptkagan.ecommerce.services;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.models.Buyer;
import com.cptkagan.ecommerce.models.Verification;
import com.cptkagan.ecommerce.repositories.VerificationRepository;

@Service
public class EmailVerificationService {
    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationRepository verificationRepository;

    public void save(String token, Buyer buyer){
        Verification verification = new Verification();
        verification.setBuyer(buyer);
        verification.setToken(token);
        verification.setCreatedAt(LocalDateTime.now());
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        verificationRepository.save(verification);
    }

    public void sendVerificationEmail(String to, String token){
        emailService.sendVerificationEmail(to, token);
    }

    public Optional<Buyer> verifyBuyer(String token) {
        Optional<Verification> verificationOpt = verificationRepository.findByToken(token);
        if(!verificationOpt.isPresent()){
            return Optional.empty();
        }

        Verification verificaiton = verificationOpt.get();
        Buyer buyer = verificaiton.getBuyer();

        if(buyer.getIsActivated()){
            return Optional.empty();
        }

        if(verificaiton.getExpiresAt().isBefore(LocalDateTime.now())){
            return Optional.empty();
        }

        verificationRepository.delete(verificaiton);

        buyer.setIsActivated(true);
        return Optional.of(buyer);
    }
}
