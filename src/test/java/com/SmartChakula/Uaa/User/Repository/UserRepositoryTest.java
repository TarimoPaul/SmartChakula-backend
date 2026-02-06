package com.SmartChakula.Uaa.User.Repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRole;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepo userRepo;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setPhone("0712345678");
        testUser.setRole(UserRole.USER);
        testUser.setIsActive(true);
    }

    @Test
    void testFindByEmail() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<UserEntity> found = userRepo.findByEmail("test@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
        assertEquals("Test User", found.get().getFullName());
        assertEquals(UserRole.USER, found.get().getRole());
    }

    @Test
    void testFindByPhone() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<UserEntity> found = userRepo.findByPhone("0712345678");

        // Then
        assertTrue(found.isPresent());
        assertEquals("0712345678", found.get().getPhone());
        assertEquals("Test User", found.get().getFullName());
    }

    @Test
    void testFindByIdentifier_WithEmail() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<UserEntity> found = userRepo.findByIdentifier("test@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByIdentifier_WithPhone() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<UserEntity> found = userRepo.findByIdentifier("0712345678");

        // Then
        assertTrue(found.isPresent());
        assertEquals("0712345678", found.get().getPhone());
    }

    @Test
    void testExistsByEmail() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        boolean exists = userRepo.existsByEmail("test@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_NotFound() {
        // When
        boolean exists = userRepo.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void testFindByUid() {
        // Given
        testUser.setUid("test-uid-123");
        entityManager.persistAndFlush(testUser);

        // When
        Optional<UserEntity> found = userRepo.findByUid("test-uid-123");

        // Then
        assertTrue(found.isPresent());
        assertEquals("test-uid-123", found.get().getUid());
    }

    @Test
    void testCreateUsersWithDifferentRoles() {
        // Create users with different roles
        UserEntity adminUser = createUserWithRole("admin@example.com", "Admin User", UserRole.ADMIN);
        UserEntity ownerUser = createUserWithRole("owner@example.com", "Owner User", UserRole.OWNER);
        UserEntity managerUser = createUserWithRole("manager@example.com", "Manager User", UserRole.MANAGER);
        UserEntity regularUser = createUserWithRole("user@example.com", "Regular User", UserRole.USER);

        // Verify all users are saved with correct roles
        assertEquals(UserRole.ADMIN, userRepo.findByEmail("admin@example.com").get().getRole());
        assertEquals(UserRole.OWNER, userRepo.findByEmail("owner@example.com").get().getRole());
        assertEquals(UserRole.MANAGER, userRepo.findByEmail("manager@example.com").get().getRole());
        assertEquals(UserRole.USER, userRepo.findByEmail("user@example.com").get().getRole());
    }

    @Test
    void testEmailUniqueness() {
        // Given
        entityManager.persistAndFlush(testUser);

        // Create another user with same email
        UserEntity duplicateUser = new UserEntity();
        duplicateUser.setFullName("Duplicate User");
        duplicateUser.setEmail("test@example.com"); // Same email
        duplicateUser.setPassword("password456");
        duplicateUser.setPhone("0712345679");
        duplicateUser.setRole(UserRole.USER);
        duplicateUser.setIsActive(true);

        // When/Then - This should throw an exception due to unique constraint
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateUser);
        });
    }

    @Test
    void testPhoneUniqueness() {
        // Given
        entityManager.persistAndFlush(testUser);

        // Create another user with same phone
        UserEntity duplicateUser = new UserEntity();
        duplicateUser.setFullName("Duplicate User");
        duplicateUser.setEmail("different@example.com");
        duplicateUser.setPassword("password456");
        duplicateUser.setPhone("0712345678"); // Same phone
        duplicateUser.setRole(UserRole.USER);
        duplicateUser.setIsActive(true);

        // When/Then - This should throw an exception due to unique constraint
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateUser);
        });
    }

    @Test
    void testUserActiveStatus() {
        // Given
        testUser.setIsActive(false);
        entityManager.persistAndFlush(testUser);

        // When
        Optional<UserEntity> found = userRepo.findByEmail("test@example.com");

        // Then
        assertTrue(found.isPresent());
        assertFalse(found.get().getIsActive());
    }

    private UserEntity createUserWithRole(String email, String fullName, UserRole role) {
        UserEntity user = new UserEntity();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword("password123");
        user.setPhone("07" + System.currentTimeMillis() % 100000000);
        user.setRole(role);
        user.setIsActive(true);
        user.setUid("uid-" + email);
        return entityManager.persistAndFlush(user);
    }
}
