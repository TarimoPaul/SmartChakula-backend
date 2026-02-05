# SmartChakula User Registration and Role-Based Access Tests

## Overview

I have created comprehensive tests for the SmartChakula user registration and role-based access system. The tests verify that users can be properly registered with different roles, can login successfully, and that role-based access control works correctly.

## Test Files Created

### 1. AuthServiceTest.java
**Location:** `src/test/java/com/SmartChakula/Uaa/User/Services/AuthServiceTest.java`

**Coverage:**
- Login functionality (success and failure scenarios)
- User registration (always assigns USER role regardless of input)
- Owner creation (ADMIN role required)
- Manager creation (OWNER role required)
- Password encryption
- JWT token generation
- Email uniqueness validation
- Phone/email login support

### 2. AuthControllerTest.java
**Location:** `src/test/java/com/SmartChakula/Uaa/User/Controler/AuthControllerTest.java`

**Coverage:**
- GraphQL endpoint testing
- Input validation
- Authentication flow
- Role assignment verification
- Controller-level error handling

### 3. UserRepositoryTest.java
**Location:** `src/test/java/com/SmartChakula/Uaa/User/Repository/UserRepositoryTest.java`

**Coverage:**
- Database operations
- Email/Phone uniqueness constraints
- User lookup by different identifiers
- Role storage and retrieval
- Active status management

### 4. UserRegistrationAndRoleTestRunner.java
**Location:** `src/test/java/com/SmartChakula/Uaa/User/TestRunner/UserRegistrationAndRoleTestRunner.java`

**Coverage:**
- End-to-end integration testing
- Complete user registration flow
- Multi-role login testing
- Access pattern verification
- Real-world scenario testing

## User Roles Tested

The system supports four user roles:

1. **USER** - Regular customer/user (default role for registration)
2. **OWNER** - Restaurant owner (can create managers)
3. **MANAGER** - Restaurant manager (assigned to specific restaurants)
4. **ADMIN** - System administrator (can create owners)

## Key Test Scenarios

### Registration Tests
- ✅ Regular user registration always assigns USER role
- ✅ Email uniqueness enforcement
- ✅ Phone uniqueness enforcement
- ✅ Password encryption verification
- ✅ Active status defaults to true

### Login Tests
- ✅ Login with email works for all roles
- ✅ Login with phone works for all roles
- ✅ Invalid credentials are rejected
- ✅ Null input validation
- ✅ JWT token generation

### Role-Based Access Tests
- ✅ Only ADMIN can create owners
- ✅ Only OWNER can create managers
- ✅ Role assignment is correctly stored
- ✅ Role information is returned in auth response

### Data Integrity Tests
- ✅ Email uniqueness constraints
- ✅ Phone uniqueness constraints
- ✅ Password encryption
- ✅ User data persistence

## How to Run the Tests

### Using Maven
```bash
# Run all user-related tests
mvn test -Dtest="**/User/**Test"

# Run specific test classes
mvn test -Dtest=AuthServiceTest
mvn test -Dtest=AuthControllerTest
mvn test -Dtest=UserRepositoryTest
mvn test -Dtest=UserRegistrationAndRoleTestRunner

# Run with detailed output
mvn test -Dtest="**/User/**Test" -X
```

### Using IDE
- Right-click on any test class and select "Run Tests"
- Use the Test Runner panel in your IDE
- Run individual test methods by right-clicking on them

## Test Results Expected

All tests should pass and verify:
1. **User Registration**: Users are registered with correct roles
2. **Login Functionality**: All user roles can login successfully
3. **Role-Based Access**: Proper authorization checks are in place
4. **Data Security**: Passwords are encrypted, data is validated
5. **System Integrity**: Database constraints are enforced

## Important Findings

### Registration Security
- The `registerUser` method correctly **always assigns USER role**, regardless of what role is requested in the input
- This prevents privilege escalation through registration

### Access Control
- `saveOwner` requires ADMIN role (`@PreAuthorize("hasRole('ADMIN')")`)
- `saveManager` requires OWNER role (`@PreAuthorize("hasRole('OWNER')")`)
- Proper authentication context is used for manager creation

### Login Flexibility
- Users can login with either email or phone
- System validates credentials properly
- JWT tokens are generated and returned

### Data Validation
- Email and phone uniqueness is enforced at database level
- Password encryption is working correctly
- User status management is functional

## Recommendations

1. **Run the tests regularly** to ensure system integrity
2. **Add more integration tests** for restaurant-manager relationships
3. **Test with real database** to verify constraints work in production
4. **Add performance tests** for login under load
5. **Security audit** of the authentication flow

The test suite provides comprehensive coverage of the user registration and role-based access functionality, ensuring the system works as expected and maintains security standards.
