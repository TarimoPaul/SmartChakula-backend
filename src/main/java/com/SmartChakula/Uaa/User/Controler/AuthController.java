package com.SmartChakula.Uaa.User.Controler;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.SmartChakula.Uaa.User.Dtos.AuthResponse;
import com.SmartChakula.Uaa.User.Dtos.LoginInput;
import com.SmartChakula.Uaa.User.Dtos.RegisterInput;
import com.SmartChakula.Uaa.User.Services.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @MutationMapping
    public AuthResponse login(@Argument LoginInput input) {
        log.info("🔴 login MUTATION CALLED with identifier: {}", input.getIdentifier());
        
        try {
            // Call service with explicit getter methods
            AuthResponse response = authService.login(
                input.getIdentifier(), 
                input.getPassword()
            );
            
            log.info("🔴 login successful, token generated");
            return response;
            
        } catch (Exception e) {
            log.error("🔴 login error: {}", e.getMessage(), e);
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    @MutationMapping
    public AuthResponse register(@Argument RegisterInput input) {
        log.info("🔴 register MUTATION CALLED");
        log.info("🔴 Register input - fullName: {}, email: {}, role: {}", 
            input.getFullName(), 
            input.getEmail(), 
            input.getRole());
        
        try {
            // Call service with explicit getter methods
            AuthResponse response = authService.register(
                input.getFullName(),
                input.getEmail(),
                input.getPassword(),
                input.getPhone(),
                input.getRole()
            );
            
            log.info("🔴 register successful, user created with uid: {}", 
                response.getUser().getUid());
            return response;
            
        } catch (Exception e) {
            log.error("🔴 register error: {}", e.getMessage(), e);
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }
}