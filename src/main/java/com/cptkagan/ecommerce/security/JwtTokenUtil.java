package com.cptkagan.ecommerce.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secret;
    private final long expiration = 36000000;

    public String generateToken(String userName, String role){
        return Jwts.builder()
                   .setSubject(userName) // Saklanacak Ana Bilgi
                   .claim("role", role)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis()+expiration))
                   .signWith(SignatureAlgorithm.HS256, secret)
                   .compact();
    }

    public Claims validateToken(String token){ 
        try{
            return Jwts.parser()
                       .setSigningKey(secret) // İmza kontrol edilir
                       .parseClaimsJws(token)
                       .getBody();
        } catch (Exception e){
            return null; // Hatalı token
        }
    }
}