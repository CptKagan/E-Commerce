package com.cptkagan.ecommerce.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.models.AbstractUser;
import com.cptkagan.ecommerce.security.JwtTokenUtil;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final BuyerService buyerService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationMaanger, JwtTokenUtil jwtTokenUtil, BuyerService buyerService, PasswordEncoder passwordEncoder){
        this.authenticationManager = authenticationMaanger;
        this.jwtTokenUtil = jwtTokenUtil;
        this.buyerService = buyerService;
        this.passwordEncoder = passwordEncoder;
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
            // user = sellerService.findByUserName(userName);
        }
        
        if(user == null || !passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid username or password");
        }

        // Generate token with username and role
        return jwtTokenUtil.generateToken(userName, user.getRole().name());
        
    }
}