package com.SmartChakula.Uaa.User.Services;

import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SmartChakula.Uaa.User.Dtos.AuthResponse;
import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import com.SmartChakula.Uaa.User.Repository.UserRepo;
import com.SmartChakula.Utils.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(String identifier, String password) {
        log.info("Login attempt for identifier: {}", identifier);

        UserEntity user = userRepo.findByIdentifier(identifier)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return AuthResponse.from(user, token);
    }

    public AuthResponse register(String fullName, String email, String password, String phone, String role) {
        log.info("Registration attempt for email: {}", email);

        // Check if user already exists
        if (userRepo.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        UserEntity newUser = new UserEntity();
        newUser.setUid(UUID.randomUUID().toString());
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password)); // Hash password
        newUser.setPhone(phone);
        newUser.setRole(UserRole.valueOf(role));
        newUser.setIsActive(true);

        // Save user to database
        UserEntity savedUser = userRepo.save(newUser);
        log.info("User registered successfully with uid: {}", savedUser.getUid());

        // Generate JWT token
        String token = jwtService.generateToken(savedUser);
        return AuthResponse.from(savedUser, token);
    }
}