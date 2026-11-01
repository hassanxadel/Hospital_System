package improved.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.security.spec.InvalidKeySpecException;

/**
 * Security utilities for password hashing, validation, and sanitization
 * Addresses critical security vulnerabilities in the original system
 * 
 * @author Hospital Management System - Improved Version
 */
public class SecurityUtils {
    
    private static final Logger LOGGER = Logger.getLogger(SecurityUtils.class.getName());
    
    // Password hashing configuration
    private static final int PBKDF2_ITERATIONS = 100000;
    private static final int SALT_LENGTH = 32;
    private static final int HASH_LENGTH = 32;
    
    // Input validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s'-]{2,50}$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,20}$"
    );
    
    private static final Pattern PATIENT_ID_PATTERN = Pattern.compile(
        "^[A-Z0-9]{5,20}$"
    );
    
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute|sp_|xp_|--|/\\*|\\*/|;|'|\"|\\b(or|and)\\b\\s+\\d+\\s*=\\s*\\d+)"
    );
    
    /**
     * Generate a cryptographically secure salt
     * @return Base64 encoded salt
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hash a password using PBKDF2 with salt
     * @param password Plain text password
     * @param salt Base64 encoded salt
     * @return Base64 encoded hash
     */
    public static String hashPassword(String password, String salt) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, PBKDF2_ITERATIONS, HASH_LENGTH * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            spec.clearPassword();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.log(Level.SEVERE, "Error hashing password", e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }
    
    /**
     * Verify a password against a stored hash
     * @param password Plain text password to verify
     * @param salt Base64 encoded salt
     * @param expectedHash Base64 encoded expected hash
     * @return true if password matches
     */
    public static boolean verifyPassword(String password, String salt, String expectedHash) {
        String actualHash = hashPassword(password, salt);
        return constantTimeEquals(actualHash, expectedHash);
    }
    
    /**
     * Constant time string comparison to prevent timing attacks
     * @param a First string
     * @param b Second string
     * @return true if strings are equal
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return ValidationResult with success status and message
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return new ValidationResult(false, "Password must be at least 8 characters long");
        }
        
        if (password.length() > 128) {
            return new ValidationResult(false, "Password must be less than 128 characters");
        }
        
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        
        if (!hasUpper) {
            return new ValidationResult(false, "Password must contain at least one uppercase letter");
        }
        if (!hasLower) {
            return new ValidationResult(false, "Password must contain at least one lowercase letter");
        }
        if (!hasDigit) {
            return new ValidationResult(false, "Password must contain at least one digit");
        }
        if (!hasSpecial) {
            return new ValidationResult(false, "Password must contain at least one special character");
        }
        
        return new ValidationResult(true, "Password is valid");
    }
    
    /**
     * Sanitize input to prevent SQL injection
     * @param input User input string
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove null bytes and control characters
        String sanitized = input.replaceAll("\0", "").replaceAll("[\r\n\t]", " ");
        
        // Trim whitespace
        sanitized = sanitized.trim();
        
        // Log potential SQL injection attempts
        if (SQL_INJECTION_PATTERN.matcher(sanitized).find()) {
            LOGGER.warning("Potential SQL injection attempt detected: " + sanitized);
            // In production, you might want to block the request entirely
        }
        
        return sanitized;
    }
    
    /**
     * Validate email format
     * @param email Email address to validate
     * @return ValidationResult
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email is required");
        }
        
        String trimmedEmail = email.trim();
        if (trimmedEmail.length() > 100) {
            return new ValidationResult(false, "Email must be less than 100 characters");
        }
        
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            return new ValidationResult(false, "Invalid email format");
        }
        
        return new ValidationResult(true, "Email is valid");
    }
    
    /**
     * Validate phone number format
     * @param phone Phone number to validate
     * @return ValidationResult
     */
    public static ValidationResult validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return new ValidationResult(false, "Phone number is required");
        }
        
        String cleanPhone = phone.replaceAll("[\\s-()]", "");
        
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            return new ValidationResult(false, "Invalid phone number format");
        }
        
        return new ValidationResult(true, "Phone number is valid");
    }
    
    /**
     * Validate name format
     * @param name Name to validate
     * @return ValidationResult
     */
    public static ValidationResult validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Name is required");
        }
        
        String trimmedName = name.trim();
        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            return new ValidationResult(false, "Name can only contain letters, spaces, hyphens, and apostrophes");
        }
        
        return new ValidationResult(true, "Name is valid");
    }
    
    /**
     * Validate username format
     * @param username Username to validate
     * @return ValidationResult
     */
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "Username is required");
        }
        
        String trimmedUsername = username.trim();
        if (!USERNAME_PATTERN.matcher(trimmedUsername).matches()) {
            return new ValidationResult(false, "Username must be 3-20 characters and contain only letters, numbers, and underscores");
        }
        
        return new ValidationResult(true, "Username is valid");
    }
    
    /**
     * Validate patient ID format
     * @param patientId Patient ID to validate
     * @return ValidationResult
     */
    public static ValidationResult validatePatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return new ValidationResult(false, "Patient ID is required");
        }
        
        String trimmedId = patientId.trim().toUpperCase();
        if (!PATIENT_ID_PATTERN.matcher(trimmedId).matches()) {
            return new ValidationResult(false, "Patient ID must be 5-20 characters and contain only letters and numbers");
        }
        
        return new ValidationResult(true, "Patient ID is valid");
    }
    
    /**
     * Validate numeric input
     * @param input String representation of number
     * @param min Minimum value
     * @param max Maximum value
     * @return ValidationResult
     */
    public static ValidationResult validateNumeric(String input, double min, double max) {
        if (input == null || input.trim().isEmpty()) {
            return new ValidationResult(false, "Numeric value is required");
        }
        
        try {
            double value = Double.parseDouble(input.trim());
            if (value < min || value > max) {
                return new ValidationResult(false, String.format("Value must be between %.2f and %.2f", min, max));
            }
            return new ValidationResult(true, "Numeric value is valid");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Invalid numeric format");
        }
    }
    
    /**
     * Escape HTML to prevent XSS attacks
     * @param input Input string
     * @return HTML-escaped string
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;")
                   .replace("/", "&#x2F;");
    }
    
    /**
     * Generate a secure random session token
     * @return Base64 encoded session token
     */
    public static String generateSessionToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * Inner class for validation results
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("ValidationResult{valid=%s, message='%s'}", valid, message);
        }
    }
} 