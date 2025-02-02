package com.cptkagan.ecommerce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// FOR CLARITY, USE hasAuthority(FULL ROLE NAME) EVERYWHERE NEXT TIME. 

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    
    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception{
        http
            .csrf().disable()
            .cors().disable()
            .authorizeRequests()
            .requestMatchers("/api/login/**", "/api/register/**", "/api/products/**", "/error").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN") // hasRole = "ROLE_" ekler, hasAuthority eklemez.
            .requestMatchers("/api/seller/**").hasRole("SELLER")
            .requestMatchers("/api/buyer/**").hasAuthority("ROLE_BUYER") // DO NOT USE HASROLE WITH ROLE_ CUZ IN SECURITY IT PREFIXES NO MATTER WHAT. IT THROWS ERROR
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .logout().logoutUrl("/logout").permitAll();

            return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception { // Kimlik doğrulama sürecinin ana bileşeni, kullanıcı adı ve şifre kontrolündeki başrol.
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}