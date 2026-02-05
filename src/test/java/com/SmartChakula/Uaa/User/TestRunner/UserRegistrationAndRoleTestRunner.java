package com.SmartChakula.Uaa.User.TestRunner;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import com.SmartChakula.Uaa.User.Repository.UserRepo;
import com.SmartChakula.Uaa.User.Services.AuthService;
import com.SmartChakula.Utils.GraphQlResponse;

@SpringBootTest
@Transactional
class UserRegistrationAndRoleTestRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Test User Registration with Different Roles")
    void testUserRegistrationRoles() {
        // Test 1: Regular user registration (should always be USER role)
        GraphQlResponse<com.SmartChakula.Uaa.User.Dtos.AuthResponse> userResponse = 
            authService.registerUser("John Doe", "john@example.com", "password123", "0712345678", "USER");
        
        assertEquals("Success", userResponse.getStatus());
        assertNotNull(userResponse.getData());
        assertEquals("USER", userResponse.getData().getUser().getRole());

        // Verify user was saved correctly
        Optional<UserEntity> savedUser = userRepo.findByEmail("john@example.com");
        assertTrue(savedUser.isPresent());
        assertEquals(UserRole.USER, savedUser.get().getRole());
        assertTrue(passwordEncoder.matches("password123", savedUser.get().getPassword()));

        // Test 2: Try to register with ADMIN role (should still be USER)
        GraphQlResponse<com.SmartChakula.Uaa.User.Dtos.AuthResponse> adminResponse = 
            authService.registerUser("Jane Doe", "jane@example.com", "password123", "0712345679", "ADMIN");
        
        assertEquals("Success", adminResponse.getStatus());
        assertEquals("USER", adminResponse.getData().getUser().getRole());

        // Verify role was set to USER despite ADMIN request
        Optional<UserEntity> janeUser = userRepo.findByEmail("jane@example.com");
        assertTrue(janeUser.isPresent());
        assertEquals(UserRole.USER, janeUser.get().getRole());
    }

    @Test
    @DisplayName("Test Login Functionality for All Roles")
    void testLoginForAllRoles() {
        // Create users with different roles for testing
        createTestUser("user@test.com", "Test User", UserRole.USER);
        createTestUser("owner@test.com", "Restaurant Owner", UserRole.OWNER);
        createTestUser("manager@test.com", "Restaurant Manager", UserRole.MANAGER);
        createTestUser("admin@test.com", "System Admin", UserRole.ADMIN);

        // Test login for each role
        String[] emails = {"user@test.com", "owner@test.com", "manager@test.com", "admin@test.com"};
        UserRole[] expectedRoles = {UserRole.USER, UserRole.OWNER, UserRole.MANAGER, UserRole.ADMIN};

        for (int i = 0; i < emails.length; i++) {
            GraphQlResponse<com.SmartChakula.Uaa.User.Dtos.AuthResponse> loginResponse = 
                authService.login(emails[i], "password123");
            
            assertEquals("Success", loginResponse.getStatus());
            assertNotNull(loginResponse.getData());
            assertNotNull(loginResponse.getData().getToken());
            assertEquals(expectedRoles[i].name(), loginResponse.getData().getUser().getRole());
            assertEquals(emails[i], loginResponse.getData().getUser().getEmail());
            assertTrue(loginResponse.getData().getUser().getIsActive());
        }
    }

    @Test
    @DisplayName("Test Login with Email and Phone")
    void testLoginWithEmailAndPhone() {
        // Create test user
        UserEntity testUser = createTestUser("phonelogin@test.com", "Phone User", UserRole.USER);

        // Test login with email
        GraphQlResponse<com.SmartChakula.Uaa.User.Dtos.AuthResponse> emailLoginResponse = 
            authService.login(testUser.getEmail(), "password123");
        
        assertEquals("Success", emailLoginResponse.getStatus());
        assertEquals(testUser.getEmail(), emailLoginResponse.getData().getUser().getEmail());

        // Test login with phone
        GraphQlResponse<com.SmartChakula.Uaa.User.Dtos.AuthResponse> phoneLoginResponse = 
            authService.login(testUser.getPhone(), "password123");
        
        assertEquals("Success", phoneLoginResponse.getStatus());
        assertEquals(testUser.getEmail(), phoneLoginResponse.getData().getUser().getEmail());
    }

    @Test
    @DisplayName("Test Login Failure Scenarios")
    void testLoginFailures() {
        // Test with non-existent user
        GraphQlResponse<com.SmartChakula.Uaa.User.Dtos.AuthResponse> nonexistentResponse = 
            authService.login("nonexistent@test.com", "password123");
        
        assertEquals("Error", nonexistentResponse.getStatus());
        assertEquals("Invalid credentials", nonexistentResponse.getMessage());
        assertNull(nonexistentResponse.getData());

        // Test with wrong password
        createTestUser("wrongpass@test.com", "Wrong Pass User", UserRole.USER);
        GraphQlResponse<com.SmartChakula.Uaa.User.Dtos.AuthResponse> wrongPassResponse = 
            authService.login("wrongpass@test.com", "wrongpassword");
        
        assertEquals("Error", wrongPassResponse.getStatus());
        assertEquals("Invalid credentials", wrongPassResponse.getMessage());
        assertNull(wrongPassResponse.getData());

        // Test with null inputs
        GraphQlResponse<com.SmartChakula.Uaa.User.Dtos.AuthResponse> nullIdResponse = 
            authService.login(null, "password123");
        
        assertEquals("Error", nullIdResponse.getStatus());
        assertEquals("Identifier is required", nullIdResponse.getMessage());

        GraphQlResponse<com.SmartChakula.Uaa.User.Dtos.AuthResponse> nullPassResponse = 
            authService.login("test@test.com", null);
        
        assertEquals("Error", nullPassResponse.getStatus());
        assertEquals("Password is required", nullPassResponse.getMessage());
    }

    @Test
    @DisplayName("Test Owner Creation")
    void testOwnerCreation() {
        // Create owner user
        GraphQlResponse<com.SmartChakula.Uaa.User.Dtos.AuthResponse> ownerResponse = 
            authService.saveOwner("Restaurant Owner", "owner@test.com", "password123", "0712345678");
        
        assertEquals("Success", ownerResponse.getStatus());
        assertNotNull(ownerResponse.getData());
        assertEquals("OWNER", ownerResponse.getData().getUser().getRole());

        // Verify owner was created
        Optional<UserEntity> savedOwner = userRepo.findByEmail("owner@test.com");
        assertTrue(savedOwner.isPresent());
        assertEquals(UserRole.OWNER, savedOwner.get().getRole());
        assertTrue(passwordEncoder.matches("password123", savedOwner.get().getPassword()));
    }

    @Test
    @DisplayName("Test Email Uniqueness")
    void testEmailUniqueness() {
        // Create first user
        createTestUser("duplicate@test.com", "First User", UserRole.USER);

        // Try to create owner with same email (should fail)
        GraphQlResponse<com.SmartChakula.Uaa.User.Dtos.AuthResponse> duplicateResponse = 
            authService.saveOwner("Second User", "duplicate@test.com", "password123", "0712345679");
        
        assertEquals("Error", duplicateResponse.getStatus());
        assertEquals("Email already in use", duplicateResponse.getMessage());
        assertNull(duplicateResponse.getData());
    }

    @Test
    @DisplayName("Test User Role Access Patterns")
    void testUserRoleAccessPatterns() {
        // Verify that regular users can only register as USER
        GraphQlResponse<com.SmartChakula.Uaa.User.Dtos.AuthResponse> regularUserResponse = 
            authService.registerUser("Regular User", "regular@test.com", "password123", "0712345678", "MANAGER");
        
        assertEquals("Success", regularUserResponse.getStatus());
        assertEquals("USER", regularUserResponse.getData().getUser().getRole()); // Should be USER, not MANAGER

        // Verify all roles exist and are properly stored
        UserRole[] allRoles = UserRole.values();
        assertEquals(4, allRoles.length); // ADMIN, USER, MANAGER, OWNER
        
        boolean hasAdmin = false, hasUser = false, hasManager = false, hasOwner = false;
        for (UserRole role : allRoles) {
            switch (role) {
                case ADMIN -> hasAdmin = true;
                case USER -> hasUser = true;
                case MANAGER -> hasManager = true;
                case OWNER -> hasOwner = true;
            }
        }
        
        assertTrue(hasAdmin && hasUser && hasManager && hasOwner);
    }

    private UserEntity createTestUser(String email, String fullName, UserRole role) {
        UserEntity user = new UserEntity();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setPhone("07" + System.currentTimeMillis() % 100000000);
        user.setRole(role);
        user.setIsActive(true);
        return userRepo.save(user);
    }
}
