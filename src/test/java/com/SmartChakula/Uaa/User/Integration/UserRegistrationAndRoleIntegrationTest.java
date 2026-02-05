package com.SmartChakula.Uaa.User.Integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import com.SmartChakula.Uaa.User.Repository.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserRegistrationAndRoleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private UserEntity adminUser;

    @BeforeEach
    void setUp() {
        // Create and save an admin user for testing protected endpoints
        adminUser = new UserEntity();
        adminUser.setFullName("Admin User");
        adminUser.setEmail("admin@smartchakula.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setPhone("0712345678");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setIsActive(true);
        adminUser = userRepo.save(adminUser);
    }

    @Test
    void testCompleteUserRegistrationFlow() throws Exception {
        // Test 1: Register a regular user
        String registerMutation = """
            mutation {
                register(input: {
                    fullName: "John Doe"
                    email: "john@example.com"
                    password: "password123"
                    phone: "0712345678"
                    role: "USER"
                }) {
                    status
                    message
                    data {
                        token
                        user {
                            uid
                            fullName
                            email
                            role
                            isActive
                        }
                    }
                }
            }
            """;

        MvcResult result = mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + registerMutation + "\"}"))
                .andExpect(status().isOk())
                .andReturn());

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("\"status\":\"Success\""));
        assertTrue(response.contains("\"role\":\"USER\""));
        assertTrue(response.contains("\"isActive\":true"));

        // Verify user was saved with correct role
        UserEntity savedUser = userRepo.findByEmail("john@example.com").orElse(null);
        assertNotNull(savedUser);
        assertEquals(UserRole.USER, savedUser.getRole());
        assertTrue(savedUser.getIsActive());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
    }

    @Test
    void testRegisterUser_AlwaysAssignsUserRole() throws Exception {
        // Even if we try to register with ADMIN role, it should be set to USER
        String registerMutation = """
            mutation {
                register(input: {
                    fullName: "Jane Doe"
                    email: "jane@example.com"
                    password: "password123"
                    phone: "0712345678"
                    role: "ADMIN"
                }) {
                    status
                    message
                    data {
                        user {
                            role
                        }
                    }
                }
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + registerMutation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.register.data.user.role").value("USER"));

        // Verify user was saved with USER role despite ADMIN request
        UserEntity savedUser = userRepo.findByEmail("jane@example.com").orElse(null);
        assertNotNull(savedUser);
        assertEquals(UserRole.USER, savedUser.getRole());
    }

    @Test
    void testLoginFlowForDifferentRoles() throws Exception {
        // Create users with different roles for testing
        createTestUserWithRole("user@test.com", "Test User", UserRole.USER);
        createTestUserWithRole("owner@test.com", "Restaurant Owner", UserRole.OWNER);
        createTestUserWithRole("manager@test.com", "Restaurant Manager", UserRole.MANAGER);

        // Test login for each role
        String[] emails = {"user@test.com", "owner@test.com", "manager@test.com"};
        String[] expectedRoles = {"USER", "OWNER", "MANAGER"};

        for (int i = 0; i < emails.length; i++) {
            String loginMutation = String.format("""
                mutation {
                    login(input: {
                        identifier: "%s"
                        password: "password123"
                    }) {
                        status
                        message
                        data {
                            token
                            user {
                                email
                                role
                                isActive
                            }
                        }
                    }
                }
                """, emails[i]);

            mockMvc.perform(post("/graphql")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"query\":\"" + loginMutation + "\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.login.status").value("Success"))
                    .andExpect(jsonPath("$.data.login.data.user.email").value(emails[i]))
                    .andExpect(jsonPath("$.data.login.data.user.role").value(expectedRoles[i]))
                    .andExpect(jsonPath("$.data.login.data.user.isActive").value(true))
                    .andExpect(jsonPath("$.data.login.data.token").exists());
        }
    }

    @Test
    void testCreateOwnerWithAdminRole() throws Exception {
        String saveOwnerMutation = """
            mutation {
                saveOwner(input: {
                    fullName: "New Restaurant Owner"
                    email: "newowner@example.com"
                    password: "password123"
                    phone: "0712345678"
                }) {
                    status
                    message
                    data {
                        user {
                            fullName
                            email
                            role
                        }
                    }
                }
            }
            """;

        mockMvc.perform(post("/graphql")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                    .encodeToString("admin@smartchakula.com:admin123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + saveOwnerMutation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.saveOwner.status").value("Success"))
                .andExpect(jsonPath("$.data.saveOwner.data.user.role").value("OWNER"));

        // Verify owner was created
        UserEntity savedOwner = userRepo.findByEmail("newowner@example.com").orElse(null);
        assertNotNull(savedOwner);
        assertEquals(UserRole.OWNER, savedOwner.getRole());
        assertEquals("New Restaurant Owner", savedOwner.getFullName());
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        String loginMutation = """
            mutation {
                login(input: {
                    identifier: "nonexistent@example.com"
                    password: "wrongpassword"
                }) {
                    status
                    message
                    data {
                        token
                    }
                }
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + loginMutation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.login.status").value("Error"))
                .andExpect(jsonPath("$.data.login.message").value("Invalid credentials"))
                .andExpect(jsonPath("$.data.login.data").doesNotExist());
    }

    @Test
    void testLoginWithNullInputs() throws Exception {
        // Test with null identifier
        String loginMutationNullId = """
            mutation {
                login(input: {
                    identifier: null
                    password: "password123"
                }) {
                    status
                    message
                }
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + loginMutationNullId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.login.status").value("Error"))
                .andExpect(jsonPath("$.data.login.message").value("Identifier is required"));

        // Test with null password
        String loginMutationNullPassword = """
            mutation {
                login(input: {
                    identifier: "test@example.com"
                    password: null
                }) {
                    status
                    message
                }
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + loginMutationNullPassword + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.login.status").value("Error"))
                .andExpect(jsonPath("$.data.login.message").value("Password is required"));
    }

    @Test
    void testUserRegistrationEmailUniqueness() throws Exception {
        // Create first user
        createTestUserWithRole("duplicate@example.com", "First User", UserRole.USER);

        // Try to create second user with same email
        String registerDuplicateMutation = """
            mutation {
                register(input: {
                    fullName: "Second User"
                    email: "duplicate@example.com"
                    password: "password123"
                    phone: "0712345678"
                    role: "USER"
                }) {
                    status
                    message
                }
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + registerDuplicateMutation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.register.status").value("Error"));
    }

    @Test
    void testLoginWithEmailOrPhone() throws Exception {
        // Create test user
        UserEntity testUser = createTestUserWithRole("phonelogin@example.com", "Phone User", UserRole.USER);

        // Test login with email
        String loginWithEmailMutation = String.format("""
            mutation {
                login(input: {
                    identifier: "%s"
                    password: "password123"
                }) {
                    status
                    data {
                        user {
                            email
                        }
                    }
                }
            }
            """, testUser.getEmail());

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + loginWithEmailMutation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.login.status").value("Success"))
                .andExpect(jsonPath("$.data.login.data.user.email").value(testUser.getEmail()));

        // Test login with phone
        String loginWithPhoneMutation = String.format("""
            mutation {
                login(input: {
                    identifier: "%s"
                    password: "password123"
                }) {
                    status
                    data {
                        user {
                            email
                        }
                    }
                }
            }
            """, testUser.getPhone());

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + loginWithPhoneMutation + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.login.status").value("Success"))
                .andExpect(jsonPath("$.data.login.data.user.email").value(testUser.getEmail()));
    }

    private UserEntity createTestUserWithRole(String email, String fullName, UserRole role) {
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
