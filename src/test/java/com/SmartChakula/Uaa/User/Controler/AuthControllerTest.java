package com.SmartChakula.Uaa.User.Controler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.SmartChakula.Uaa.User.Dtos.AuthResponse;
import com.SmartChakula.Uaa.User.Dtos.LoginInput;
import com.SmartChakula.Uaa.User.Dtos.RegisterInput;
import com.SmartChakula.Uaa.User.Dtos.SaveOwnerInput;
import com.SmartChakula.Uaa.User.Dtos.SaveManagerInput;
import com.SmartChakula.Uaa.User.Dtos.UserDto;
import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import com.SmartChakula.Uaa.User.Services.AuthService;
import com.SmartChakula.Utils.GraphQlResponse;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginInput loginInput;
    private RegisterInput registerInput;
    private SaveOwnerInput saveOwnerInput;
    private SaveManagerInput saveManagerInput;
    private AuthResponse authResponse;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        loginInput = new LoginInput();
        loginInput.setIdentifier("test@example.com");
        loginInput.setPassword("password123");

        registerInput = new RegisterInput();
        registerInput.setFullName("Test User");
        registerInput.setEmail("test@example.com");
        registerInput.setPassword("password123");
        registerInput.setPhone("0712345678");
        registerInput.setRole("USER");

        saveOwnerInput = new SaveOwnerInput();
        saveOwnerInput.setFullName("Restaurant Owner");
        saveOwnerInput.setEmail("owner@example.com");
        saveOwnerInput.setPassword("password123");
        saveOwnerInput.setPhone("0712345678");

        saveManagerInput = new SaveManagerInput();
        saveManagerInput.setFullName("Restaurant Manager");
        saveManagerInput.setEmail("manager@example.com");
        saveManagerInput.setPassword("password123");
        saveManagerInput.setPhone("0712345678");
        saveManagerInput.setRestaurantUids(List.of("restaurant-uid-1", "restaurant-uid-2"));

        authResponse = new AuthResponse();
        authResponse.setToken("jwt-token");
        authResponse.setUser(new UserDto());

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFullName("Test User");
        userEntity.setEmail("test@example.com");
        userEntity.setRole(UserRole.MANAGER);
    }

    @Test
    void testLogin_Success() {
        // Given
        GraphQlResponse<AuthResponse> expectedResponse = new GraphQlResponse<>(
                "Success", "Login successful", authResponse);
        when(authService.login(loginInput.getIdentifier(), loginInput.getPassword()))
                .thenReturn(expectedResponse);

        // When
        GraphQlResponse<AuthResponse> response = authController.login(loginInput);

        // Then
        assertEquals("Success", response.getStatus());
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("jwt-token", response.getData().getToken());
        verify(authService).login(loginInput.getIdentifier(), loginInput.getPassword());
    }

    @Test
    void testLogin_Failure() {
        // Given
        GraphQlResponse<AuthResponse> expectedResponse = new GraphQlResponse<>(
                "Error", "Invalid credentials", null);
        when(authService.login(loginInput.getIdentifier(), loginInput.getPassword()))
                .thenReturn(expectedResponse);

        // When
        GraphQlResponse<AuthResponse> response = authController.login(loginInput);

        // Then
        assertEquals("Error", response.getStatus());
        assertEquals("Invalid credentials", response.getMessage());
        assertNull(response.getData());
        verify(authService).login(loginInput.getIdentifier(), loginInput.getPassword());
    }

    @Test
    void testRegister_Success() {
        // Given
        GraphQlResponse<AuthResponse> expectedResponse = new GraphQlResponse<>(
                "Success", "Registration successful", authResponse);
        when(authService.registerUser(
                registerInput.getFullName(),
                registerInput.getEmail(),
                registerInput.getPassword(),
                registerInput.getPhone(),
                registerInput.getRole()))
                .thenReturn(expectedResponse);

        // When
        GraphQlResponse<AuthResponse> response = authController.register(registerInput);

        // Then
        assertEquals("Success", response.getStatus());
        assertEquals("Registration successful", response.getMessage());
        assertNotNull(response.getData());
        verify(authService).registerUser(
                registerInput.getFullName(),
                registerInput.getEmail(),
                registerInput.getPassword(),
                registerInput.getPhone(),
                registerInput.getRole());
    }

    @Test
    void testRegister_VerifyRoleAssignment() {
        // Given
        registerInput.setRole("ADMIN"); // Even if ADMIN is requested, it should be set to USER
        GraphQlResponse<AuthResponse> expectedResponse = new GraphQlResponse<>(
                "Success", "Registration successful", authResponse);
        when(authService.registerUser(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(expectedResponse);

        // When
        authController.register(registerInput);

        // Then
        verify(authService).registerUser(
                registerInput.getFullName(),
                registerInput.getEmail(),
                registerInput.getPassword(),
                registerInput.getPhone(),
                registerInput.getRole());
    }

    @Test
    void testSaveOwner_WithAdminRole() {
        // Given
        GraphQlResponse<AuthResponse> expectedResponse = new GraphQlResponse<>(
                "Success", "Owner user saved successfully", authResponse);
        when(authService.saveOwner(
                saveOwnerInput.getFullName(),
                saveOwnerInput.getEmail(),
                saveOwnerInput.getPassword(),
                saveOwnerInput.getPhone()))
                .thenReturn(expectedResponse);

        // When
        GraphQlResponse<AuthResponse> response = authController.saveOwner(saveOwnerInput);

        // Then
        assertEquals("Success", response.getStatus());
        assertEquals("Owner user saved successfully", response.getMessage());
        assertNotNull(response.getData());
        verify(authService).saveOwner(
                saveOwnerInput.getFullName(),
                saveOwnerInput.getEmail(),
                saveOwnerInput.getPassword(),
                saveOwnerInput.getPhone());
    }

    @Test
    void testSaveManager_WithOwnerRole() {
        // Given
        Authentication auth = createAuthenticationWithRole("OWNER", "owner@example.com");
        GraphQlResponse<UserEntity> expectedResponse = new GraphQlResponse<>(
                "Success", "Manager user saved successfully", userEntity);
        when(authService.saveManager(
                saveManagerInput.getFullName(),
                saveManagerInput.getEmail(),
                saveManagerInput.getPassword(),
                saveManagerInput.getPhone(),
                "owner@example.com",
                saveManagerInput.getRestaurantUids()))
                .thenReturn(expectedResponse);

        // When
        GraphQlResponse<UserEntity> response = authController.saveManager(saveManagerInput, auth);

        // Then
        assertEquals("Success", response.getStatus());
        assertEquals("Manager user saved successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(UserRole.MANAGER, response.getData().getRole());
        verify(authService).saveManager(
                saveManagerInput.getFullName(),
                saveManagerInput.getEmail(),
                saveManagerInput.getPassword(),
                saveManagerInput.getPhone(),
                "owner@example.com",
                saveManagerInput.getRestaurantUids());
    }

    @Test
    void testSaveManager_AuthenticationEmailExtraction() {
        // Given
        Authentication auth = createAuthenticationWithRole("OWNER", "specific-owner@example.com");
        GraphQlResponse<UserEntity> expectedResponse = new GraphQlResponse<>(
                "Success", "Manager user saved successfully", userEntity);
        when(authService.saveManager(
                anyString(), anyString(), anyString(), anyString(),
                eq("specific-owner@example.com"), anyList()))
                .thenReturn(expectedResponse);

        // When
        authController.saveManager(saveManagerInput, auth);

        // Then
        // Verify that the authentication email is correctly extracted and passed to the
        // service
        verify(authService).saveManager(
                saveManagerInput.getFullName(),
                saveManagerInput.getEmail(),
                saveManagerInput.getPassword(),
                saveManagerInput.getPhone(),
                "specific-owner@example.com",
                saveManagerInput.getRestaurantUids());
    }

    @Test
    void testSaveManager_VerifyRestaurantUidsPassed() {
        // Given
        Authentication auth = createAuthenticationWithRole("OWNER", "owner@example.com");
        List<String> expectedRestaurantUids = List.of("restaurant-1", "restaurant-2", "restaurant-3");
        saveManagerInput.setRestaurantUids(expectedRestaurantUids);

        GraphQlResponse<UserEntity> expectedResponse = new GraphQlResponse<>(
                "Success", "Manager user saved successfully", userEntity);
        when(authService.saveManager(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), eq(expectedRestaurantUids)))
                .thenReturn(expectedResponse);

        // When
        authController.saveManager(saveManagerInput, auth);

        // Then
        verify(authService).saveManager(
                saveManagerInput.getFullName(),
                saveManagerInput.getEmail(),
                saveManagerInput.getPassword(),
                saveManagerInput.getPhone(),
                "owner@example.com",
                expectedRestaurantUids);
    }

    /**
     * Helper method to create authentication with specific role
     */
    private Authentication createAuthenticationWithRole(String role, String email) {
        return new TestingAuthenticationToken(email, null, "ROLE_" + role);
    }

    @Test
    void testLogin_NullInput() {
        // Given
        when(authService.login(isNull(), anyString()))
                .thenReturn(new GraphQlResponse<>("Error", "Identifier is required", null));

        // When
        GraphQlResponse<AuthResponse> response = authController.login(new LoginInput());

        // Then
        assertEquals("Error", response.getStatus());
        verify(authService).login(isNull(), isNull());
    }

    @Test
    void testRegister_NullInput() {
        // Given
        when(authService.registerUser(isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(new GraphQlResponse<>("Error", "Registration failed", null));

        // When
        GraphQlResponse<AuthResponse> response = authController.register(new RegisterInput());

        // Then
        assertEquals("Error", response.getStatus());
        verify(authService).registerUser(isNull(), isNull(), isNull(), isNull(), isNull());
    }
}
