package lms.domain.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for password hashing and verification.
 * 
 * <p>Uses SHA-512 hashing with Base64 encoding. Note: Does not use salt.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class PasswordUtils {

	/**
	 * Hashes a password using SHA-512.
	 * 
	 * @param password the password to hash
	 * @return the Base64-encoded hash
	 */
	public static String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] hashedBytes = md.digest(password.getBytes());
			return Base64.getEncoder().encodeToString(hashedBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Verifies if a password matches a hash.
	 * 
	 * @param password       the password to verify
	 * @param hashedPassword the stored hash
	 * @return true if passwords match, false otherwise
	 */
	public static boolean verifyPassword(String password, String hashedPassword) {
		return hashPassword(password).equals(hashedPassword);
	}
}
