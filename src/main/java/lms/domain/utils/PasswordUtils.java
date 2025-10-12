package lms.domain.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for hashing and verifying passwords.
 * 
 * <p>This class provides static methods to hash passwords using SHA-512
 * and to verify raw passwords against stored hashed passwords.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * String hashed = PasswordUtils.hashPassword("myPassword");
 * boolean valid = PasswordUtils.verifyPassword("myPassword", hashed);
 * </pre>
 * 
 * Note: This implementation does not use a salt.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class PasswordUtils {
	
	
	

	
    /**
     * Hashes a raw password using SHA-512 and encodes it in Base64.
     * 
     * @param password the raw password to hash
     * @return the Base64-encoded hash of the password
     */
    public static String hashPassword(String password) 
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    

    
    /**
     * Verifies if a raw password matches a previously hashed password.
     * 
     * @param password the raw password to verify
     * @param hashedPassword the previously hashed password
     * @return {@code true} if the passwords match, {@code false} otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }
}
