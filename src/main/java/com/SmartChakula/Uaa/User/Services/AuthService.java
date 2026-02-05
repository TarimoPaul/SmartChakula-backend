package com.SmartChakula.Uaa.User.Services;

import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SmartChakula.Restaurant.Entity.RestaurantEntity;
import com.SmartChakula.Restaurant.Repository.RestaurantRepo;
import com.SmartChakula.Uaa.User.Dtos.AuthResponse;
import com.SmartChakula.Uaa.User.Dtos.UserDto;
import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRestaurant;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import com.SmartChakula.Uaa.User.Repository.UserRepo;
import com.SmartChakula.Uaa.User.Repository.UserRestaurantRepo;
import com.SmartChakula.Utils.GraphQlResponse;
import com.SmartChakula.Utils.ResponseStatus;
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
    private final RestaurantRepo restaurantRepo;
    private final UserRestaurantRepo userRestaurantRepo;
    public GraphQlResponse<AuthResponse> login(String identifier, String password) {
        try {
            log.info("Login attempt for identifier: {}", identifier);

            if (identifier == null || identifier.isBlank()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Identifier is required",
                        null);
            }

            if (password == null || password.isBlank()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Password is required",
                        null);
            }

            UserEntity user = userRepo.findByIdentifier(identifier).orElse(null);
            if (user == null) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Invalid credentials",
                        null);
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Invalid credentials",
                        null);
            }

            String token = jwtService.generateToken(user);
            AuthResponse authResponse = AuthResponse.from(user, token);

            log.info("Login successful for user: {}", user.getUid());
            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Login successful",
                    authResponse);
        } catch (Exception e) {
            log.error("Error in login: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Login failed: " + e.getMessage(),
                    null);
        }
    }

    @Transactional
    public GraphQlResponse<AuthResponse> registerUser(String fullName, String email, String password, String phone,
            String role) {
        try {

            // Create new user
            UserEntity newUser = new UserEntity();
            newUser.setUid(UUID.randomUUID().toString());
            newUser.setFullName(fullName.trim());
            newUser.setEmail(email.trim());
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setPhone(phone);
            newUser.setRole(UserRole.USER);
            newUser.setIsActive(true);

            // Save user to database
            UserEntity savedUser = userRepo.save(newUser);

            log.info("User registered successfully with uid: {}", savedUser.getUid());

            // Generate JWT token
            String token = jwtService.generateToken(savedUser);
            AuthResponse authResponse = AuthResponse.from(savedUser, token);

            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Registration successful",
                    authResponse);
        } catch (Exception e) {
            log.error("Error in register: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Registration failed: " + e.getMessage(),
                    null);
        }
    }

    @Transactional
    public GraphQlResponse<AuthResponse> saveOwner(String fullName, String email, String password, String phone ) {
        try {


            if (userRepo.findByEmail(email).isPresent()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Email already in use",
                        null
                        
                        );
                
            }

            // Create new owner user
            UserEntity owner = new UserEntity();
            owner.setUid(UUID.randomUUID().toString());
            owner.setFullName(fullName.trim());
            owner.setEmail(email.trim());
            owner.setPassword(passwordEncoder.encode(password));
            owner.setPhone(phone);
            owner.setRole(UserRole.OWNER);
            owner.setIsActive(true);

            // Save user to database
            userRepo.save(owner);

            log.info("Owner user saved successfully with uid: {}", owner.getUid());

            // Generate JWT token
            String token = jwtService.generateToken(owner);

            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Owner user saved successfully",
                    AuthResponse.from(owner, token));
        } catch (Exception e) {
            log.error("Error in saveOwner: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Saving owner user failed: " + e.getMessage(),
                    null);
        }

    }


    public GraphQlResponse<UserEntity> saveManager(String fullName, String email, String password, String phone,
            String ownerEmail, java.util.List<String> restaurantUids) {
        try {

            if (userRepo.findByEmail(email).isPresent()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Email already in use",
                        null
                        
                        );
                
            }

            UserEntity owner = userRepo.findByEmail(ownerEmail).orElse(null);
            if (owner == null || owner.getRole() != UserRole.OWNER) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Only owners can create manager accounts",
                        null);
            }

            // Create new manager user
            UserEntity manager = new UserEntity();
            manager.setUid(UUID.randomUUID().toString());
            manager.setFullName(fullName.trim());
            manager.setEmail(email.trim());
            manager.setPassword(passwordEncoder.encode(password));
            manager.setPhone(phone);
            manager.setRole(UserRole.MANAGER);
            manager.setIsActive(true);

            userRepo.save(manager);
            log.info("Manager user saved successfully with uid: {}", manager.getUid());

            for (String restaurantUid : restaurantUids) {

                RestaurantEntity restaurant = restaurantRepo.findByUid(restaurantUid)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with uid: " + restaurantUid)
                
                );

                if (!restaurant.getOwner().getUid().equals(owner.getUid())) {
                    return new GraphQlResponse<>(
                            ResponseStatus.Error.toString(),
                            "Owner does not own restaurant with uid: " + restaurantUid,
                            null);
                    
                }

                UserRestaurant ur = new UserRestaurant();
                ur.setUser(manager);
                ur.setRestaurant(restaurant);
                ur.setRole(UserRole.MANAGER);

                userRestaurantRepo.save(ur);

            }


            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Manager user saved successfully",
                    manager);
        } catch (Exception e) {
            log.error("Error in saveManager: {}", e.getMessage(), e);   

        }     return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Saving manager user failed: " ,
                    null);
        }



}