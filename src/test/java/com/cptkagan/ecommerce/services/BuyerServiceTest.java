package com.cptkagan.ecommerce.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cptkagan.ecommerce.DTOs.requestDTO.BuyerRegisterRequest;
import com.cptkagan.ecommerce.repositories.BuyerRepository;

@ExtendWith(MockitoExtension.class)
public class BuyerServiceTest {
    @InjectMocks
    private BuyerService buyerService;

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Test
    void testRegisterBuyer_usernameExists(){
        BuyerRegisterRequest request = new BuyerRegisterRequest();
        request.setUserName("existingUser");
        request.setPassword("password123");
        request.setEmail("mkagankayabasi2@gmail.com");
        request.setGender(true);
        request.setFirstName("testFirstName");
        request.setLastName("testLastName");

        when(buyerRepository.existsByUserName("existingUser")).thenReturn(true);

        ResponseEntity<?> response = buyerService.registerBuyer(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username is already taken", response.getBody());
    }

    @Test
    void testRegisterBuyer_emailExists(){
        BuyerRegisterRequest request = new BuyerRegisterRequest();
        request.setUserName("existingUser");
        request.setPassword("password123");
        request.setEmail("mkagankayabasi2@gmail.com");
        request.setGender(true);
        request.setFirstName("testFirstName");
        request.setLastName("testLastName");

        when(buyerRepository.existsByEmail("mkagankayabasi2@gmail.com")).thenReturn(true);
        
        ResponseEntity<?> response = buyerService.registerBuyer(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is already taken", response.getBody());
    }

    @Test
    void testRegisterBuyer_success(){
        BuyerRegisterRequest request = new BuyerRegisterRequest();
        request.setUserName("existingUser");
        request.setPassword("password123");
        request.setEmail("mkagankayabasi2@gmail.com");
        request.setGender(true);
        request.setFirstName("testFirstName");
        request.setLastName("testLastName");

        when(buyerRepository.existsByUserName("existingUser")).thenReturn(false);
        when(buyerRepository.existsByEmail("mkagankayabasi2@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        
        ResponseEntity<?> response = buyerService.registerBuyer(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully, please check your email to verify your account!", response.getBody());
    }
}
