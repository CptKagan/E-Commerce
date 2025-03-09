package com.cptkagan.ecommerce.services;

import javax.management.RuntimeErrorException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.models.AbstractUser;
import com.cptkagan.ecommerce.models.Seller;
import com.cptkagan.ecommerce.security.JwtTokenUtil;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final BuyerService buyerService;
    private final PasswordEncoder passwordEncoder;
    private final SellerService sellerService;
    private final AdminService adminService;

    public AuthService(AuthenticationManager authenticationMaanger, JwtTokenUtil jwtTokenUtil, BuyerService buyerService, PasswordEncoder passwordEncoder, SellerService sellerService, AdminService adminService){
        this.authenticationManager = authenticationMaanger;
        this.jwtTokenUtil = jwtTokenUtil;
        this.buyerService = buyerService;
        this.passwordEncoder = passwordEncoder;
        this.sellerService = sellerService;
        this.adminService = adminService;
    }

    public void authenticate(String userName, String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password)); // Giriş yapmaya çalışan kişinin kimliğinin doğrulanması
    }

    public String login(String userName, String password, String userType) {
        AbstractUser user = null;
        if(userType.equals("BUYER")){
            user = buyerService.findByUserName(userName);
        }
        else if(userType.equals("SELLER")){
            user = sellerService.findByUserName(userName);
        }
        else if(userType.equals("ADMIN")){
            user = adminService.findByUserName(userName);
        }
        
        if(user == null || !passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid username or password");
        }

        if(!user.isEligibleForLogin()){
            throw new RuntimeException("Account is not activated! Please check your e-mail or contact via support: cptkagantestecommerce@gmail.com");
        }

        // Generate token with username and role
        return jwtTokenUtil.generateToken(userName, user.getRole().name());
    }
}