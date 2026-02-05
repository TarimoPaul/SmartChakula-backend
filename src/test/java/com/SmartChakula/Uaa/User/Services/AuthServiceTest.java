package com.SmartChakula.Uaa.User.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.SmartChakula.Restaurant.Entity.RestaurantEntity;
import com.SmartChakula.Restaurant.Repository.RestaurantRepo;
import com.SmartChakula.Uaa.User.Dtos.AuthResponse;
import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import com.SmartChakula.Uaa.User.Repository.UserRepo;
import com.SmartChakula.Uaa.User.Repository.UserRestaurantRepo;
import com.SmartChakula.Utils.GraphQlResponse;
import com.SmartChakula.Utils.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RestaurantRepo restaurantRepo;

    @Mock
    private UserRestaurantRepo userRestaurantRepo;

    @InjectMocks
    private AuthService authService;

    private UserEntity testUser;
    private RestaurantEntity testRestaurant;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUid(UUID.randomUUID().toString());
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPhone("0712345678");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.USER);
        testUser.setIsActive(true);

        testRestaurant = new RestaurantEntity();
        testRestaurant.setUid(UUID.randomUUID().toString());
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setOwner(testUser);
    }

    @Test
    void testLogin_Success() {
        // Given
        String identifier = "test@example.com";
        String password = "password123";

        when(userRepo.findByIdentifier(identifier)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("jwt-token");

        // When
        GraphQlResponse<AuthResponse> response = authService.login(identifier, password);

        // Then
        assertEquals("Success", response.getStatus());
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("jwt-token", response.getData().getToken());
        verify(userRepo).findByIdentifier(identifier);
        verify(passwordEncoder).matches(password, testUser.getPassword());
        verify(jwtService).generateToken(testUser);
    }

    @Test
    void testLogin_UserNotFound() {
        // Given
        String identifier = "nonexistent@example.com";
        String password = "password123";

        when(userRepo.findByIdentifier(identifier)).thenReturn(Optional.empty());

        // When
        GraphQlResponse<AuthResponse> response = authService.login(identifier, password);

        // Then
        assertEquals("Error", response.getStatus());
        assertEquals("Invalid credentials", response.getMessage());
        assertNull(response.getData());
        verify(userRepo).findByIdentifier(identifier);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testLogin_WrongPassword() {
        // Given
        String identifier = "test@example.com";
        String password = "wrongpassword";

        when(userRepo.findByIdentifier(identifier)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(false);

        // When
        GraphQlResponse<AuthResponse> response = authService.login(identifier, password);

        // Then
        assertEquals("Error", response.getStatus());
        assertEquals("Invalid credentials", response.getMessage());
        assertNull(response.getData());
        verify(userRepo).findByIdentifier(identifier);
        verify(passwordEncoder).matches(password, testUser.getPassword());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testLogin_NullIdentifier() {
        // When
        GraphQlResponse<AuthResponse> response = authService.login(null, "password123");

        // Then
        assertEquals("Error", response.getStatus());
        assertEquals("Identifier is required", response.getMessage());
        assertNull(response.getData());
        verify(userRepo, never()).findByIdentifier(any());
    }

    @Test
    void testLogin_NullPassword() {
        // When
        GraphQlResponse<AuthResponse> response = authService.login("test@example.com", null);

        // Then
        assertEquals("Error", response.getStatus());
        assertEquals("Password is required", response.getMessage());
        assertNull(response.getData());
        verify(userRepo, never()).findByIdentifier(any());
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        String fullName = "New User";
        String email = "newuser@example.com";
        String password = "password123";
        String phone = "0712345678";
        String role = "USER";

        when(userRepo.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            savedUser.setUid(UUID.randomUUID().toString());
            return savedUser;
        });
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("jwt-token");

        // When
        GraphQlResponse<AuthResponse> response = authService.registerUser(fullName, email, password, phone, role);

        // Then
        assertEquals("Success", response.getStatus());
        assertEquals("Registration successful", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("jwt-token", response.getData().getToken());
        verify(userRepo).save(any(UserEntity.class));
        verify(passwordEncoder).encode(password);
        verify(jwtService).generateToken(any(UserEntity.class));
    }

    @Test
    void testRegisterUser_VerifyRoleIsSetToUSER() {
        // Given
        when(userRepo.save(any(UserEntity.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("jwt-token");

        // When
        authService.registerUser("Test", "test@example.com", "password", "0712345678", "ADMIN");

        // Then
        verify(userRepo).save(argThat(user -> user.getRole() == UserRole.USER));
    }

    @Test
    void testSaveOwner_Success() {
        // Given
        String fullName = "Restaurant Owner";
        String email = "owner@example.com";
        String password = "password123";
        String phone = "0712345678";

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepo.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity savedOwner = invocation.getArgument(0);
            savedOwner.setId(3L);
            savedOwner.setUid(UUID.randomUUID().toString());
            return savedOwner;
        });
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("jwt-token");

        // When
        GraphQlResponse<AuthResponse> response = authService.saveOwner(fullName, email, password, phone);

        // Then
        assertEquals("Success", response.getStatus());
        assertEquals("Owner user saved successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("jwt-token", response.getData().getToken());
        verify(userRepo).save(argThat(user -> user.getRole() == UserRole.OWNER));
        verify(passwordEncoder).encode(password);
        verify(jwtService).generateToken(any(UserEntity.class));
    }

    @Test
    void testSaveOwner_EmailAlreadyExists() {
        // Given
        String email = "existing@example.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        GraphQlResponse<AuthResponse> response = authService.saveOwner("Test", email, "password", "0712345678");

        // Then
        assertEquals("Error", response.getStatus());
        assertEquals("Email already in use", response.getMessage());
        assertNull(response.getData());
        verify(userRepo).findByEmail(email);
        verify(userRepo, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testSaveManager_Success() {
        // Given
        String fullName = "Restaurant Manager";
        String email = "manager@example.com";
        String password = "password123";
        String phone = "0712345678";
        String ownerEmail = "owner@example.com";
        String restaurantUid = testRestaurant.getUid();

        UserEntity owner = new UserEntity();
        owner.setRole(UserRole.OWNER);
        owner.setUid(UUID.randomUUID().toString());

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepo.findByEmail(ownerEmail)).thenReturn(Optional.of(owner));
        when(restaurantRepo.findByUid(restaurantUid)).thenReturn(Optional.of(testRestaurant));
        when(userRepo.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity savedManager = invocation.getArgument(0);
            savedManager.setId(4L);
            savedManager.setUid(UUID.randomUUID().toString());
            return savedManager;
        });
        when(testRestaurant.getOwner()).thenReturn(owner);

        // When
        GraphQlResponse<UserEntity> response = authService.saveManager(
                fullName, email, password, phone, ownerEmail, java.util.List.of(restaurantUid));

        // Then
        assertEquals("Success", response.getStatus());
        assertEquals("Manager user saved successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(UserRole.MANAGER, response.getData().getRole());
        verify(userRepo).save(argThat(user -> user.getRole() == UserRole.MANAGER));
        verify(userRestaurantRepo).save(any());
        verify(passwordEncoder).encode(password);
    }

    @Test
    void testSaveManager_EmailAlreadyExists() {
        // Given
        String email = "existing@example.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        GraphQlResponse<UserEntity> response = authService.saveManager(
                "Test", email, "password", "0712345678", "owner@example.com", java.util.List.of("restaurant-uid"));

        // Then
        assertEquals("Error", response.getStatus());
        assertEquals("Email already in use", response.getMessage());
        assertNull(response.getData());
        verify(userRepo).findByEmail(email);
        verify(userRepo, never()).save(any());
    }

    @Test
    void testSaveManager_OwnerNotFound() {
        // Given
        String ownerEmail = "nonexistent-owner@example.com";
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        // When
        GraphQlResponse<UserEntity> response = authService.saveManager(
                "Test", "manager@example.com", "password", "0712345678", ownerEmail,
                java.util.List.of("restaurant-uid"));

        // Then
        assertEquals("Error", response.getStatus());
        assertEquals("Only owners can create manager accounts", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testSaveManager_OwnerDoesNotOwnRestaurant() {
        // Given
        String ownerEmail = "owner@example.com";
        String restaurantUid = testRestaurant.getUid();

        UserEntity differentOwner = new UserEntity();
        differentOwner.setRole(UserRole.OWNER);
        differentOwner.setUid(UUID.randomUUID().toString());

        UserEntity owner = new UserEntity();
        owner.setRole(UserRole.OWNER);
        owner.setUid(UUID.randomUUID().toString());

        when(userRepo.findByEmail("manager@example.com")).thenReturn(Optional.empty());
        when(userRepo.findByEmail(ownerEmail)).thenReturn(Optional.of(owner));
        when(restaurantRepo.findByUid(restaurantUid)).thenReturn(Optional.of(testRestaurant));
        when(testRestaurant.getOwner()).thenReturn(differentOwner); // Different owner

        // When
        GraphQlResponse<UserEntity> response = authService.saveManager(
                "Test", "manager@example.com", "password", "0712345678", ownerEmail, java.util.List.of(restaurantUid));

        // Then
        assertEquals("Error", response.getStatus());
        assertTrue(response.getMessage().contains("Owner does not own restaurant"));
        assertNull(response.getData());
    }
}
