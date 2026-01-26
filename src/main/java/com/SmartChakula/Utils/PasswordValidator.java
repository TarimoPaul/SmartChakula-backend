package com.SmartChakula.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for password validation
 * Provides comprehensive password strength validation
 */
public class PasswordValidator {

    // Minimum password length
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;

    // Regex patterns for password requirements
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[@$!%*?&].*");

    // Common weak passwords to reject
    private static final String[] COMMON_WEAK_PASSWORDS = {
        "password", "12345678", "qwerty", "admin123", "letmein",
        "welcome", "monkey", "dragon", "master", "sunshine"
    };

    /**
     * Validate password strength
     *
     * @param password Password to validate
     * @return ValidationResult containing validation status and error messages
     */
    public static ValidationResult validate(String password) {
        List<String> errors = new ArrayList<>();

        // Check if password is null or empty
        if (password == null || password.trim().isEmpty()) {
            errors.add("Password cannot be empty");
            return new ValidationResult(false, errors);
        }

        // Check minimum length
        if (password.length() < MIN_LENGTH) {
            errors.add(String.format("Password must be at least %d characters long", MIN_LENGTH));
        }

        // Check maximum length
        if (password.length() > MAX_LENGTH) {
            errors.add(String.format("Password must not exceed %d characters", MAX_LENGTH));
        }

        // Check for uppercase letter
        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one uppercase letter (A-Z)");
        }

        // Check for lowercase letter
        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one lowercase letter (a-z)");
        }

        // Check for digit
        if (!DIGIT_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one number (0-9)");
        }

        // Check for special character
        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            errors.add("Password must contain at least one special character (@$!%*?&)");
        }

        // Check for common weak passwords
        String lowerPassword = password.toLowerCase();
        for (String weakPassword : COMMON_WEAK_PASSWORDS) {
            if (lowerPassword.contains(weakPassword)) {
                errors.add("Password is too common and easily guessable");
                break;
            }
        }

        // Check for sequential characters (e.g., "12345", "abcde")
        if (hasSequentialCharacters(password)) {
            errors.add("Password should not contain sequential characters");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Quick validation check - returns true if password meets requirements
     *
     * @param password Password to validate
     * @return true if password is valid, false otherwise
     */
    public static boolean isValid(String password) {
        return validate(password).isValid();
    }

    /**
     * Check if password contains sequential characters
     *
     * @param password Password to check
     * @return true if password has sequential characters
     */
    private static boolean hasSequentialCharacters(String password) {
        if (password.length() < 3) {
            return false;
        }

        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);

            // Check for ascending sequence
            if (c2 == c1 + 1 && c3 == c2 + 1) {
                return true;
            }

            // Check for descending sequence
            if (c2 == c1 - 1 && c3 == c2 - 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get password strength score (0-5)
     *
     * @param password Password to evaluate
     * @return Strength score from 0 (very weak) to 5 (very strong)
     */
    public static int getStrengthScore(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int score = 0;

        // Length bonus
        if (password.length() >= MIN_LENGTH) score++;
        if (password.length() >= 12) score++;

        // Character type bonuses
        if (UPPERCASE_PATTERN.matcher(password).matches()) score++;
        if (LOWERCASE_PATTERN.matcher(password).matches()) score++;
        if (DIGIT_PATTERN.matcher(password).matches()) score++;
        if (SPECIAL_CHAR_PATTERN.matcher(password).matches()) score++;

        // Penalty for weak passwords
        String lowerPassword = password.toLowerCase();
        for (String weakPassword : COMMON_WEAK_PASSWORDS) {
            if (lowerPassword.contains(weakPassword)) {
                score = Math.max(0, score - 2);
                break;
            }
        }

        return Math.min(5, score);
    }

    /**
     * Get password strength description
     *
     * @param password Password to evaluate
     * @return Human-readable strength description
     */
    public static String getStrengthDescription(String password) {
        int score = getStrengthScore(password);
        switch (score) {
            case 0:
            case 1:
                return "Very Weak";
            case 2:
                return "Weak";
            case 3:
                return "Fair";
            case 4:
                return "Strong";
            case 5:
                return "Very Strong";
            default:
                return "Unknown";
        }
    }

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            return String.join(". ", errors);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }
}
