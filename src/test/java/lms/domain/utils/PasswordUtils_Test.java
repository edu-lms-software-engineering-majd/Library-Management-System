package lms.domain.utils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.utils.PasswordUtils;

/**
 * Unit tests for the {@link PasswordUtils} utility class.
 * 
 * <p>
 * This test class verifies the password hashing and verification functionality
 * used for secure user authentication in the Library Management System.
 * </p>
 *
 * <h2>Test Coverage:</h2>
 * <ul>
 * <li>Password hashing produces non-null, non-empty results</li>
 * <li>Hashed passwords differ from original plain text</li>
 * <li>Password verification for correct and incorrect passwords</li>
 * <li>Consistency of hash results for same input</li>
 * <li>Security properties of the hashing algorithm</li>
 * </ul>
 *
 * <h2>Security Notes:</h2>
 * <ul>
 * <li>Uses SHA-512 hashing algorithm without salt</li>
 * <li>Hashes are Base64 encoded for storage</li>
 * <li>For production use, consider adding salt for additional security</li>
 * </ul>
 *
 * @author Majd Awwad
 * @version 1.0
 * @see PasswordUtils
 */
class PasswordUtils_Test {

	/**
	 * Sets up test environment before all test methods. Currently not used but
	 * available for future expansion.
	 *
	 * @throws Exception if setup fails
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// Reserved for future global test setup
	}

	/**
	 * Cleans up test environment after all test methods. Currently not used but
	 * available for future expansion.
	 *
	 * @throws Exception if cleanup fails
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		// Reserved for future global test cleanup
	}

	/**
	 * Sets up test environment before each test method. Currently not used but
	 * available for future expansion.
	 *
	 * @throws Exception if setup fails
	 */
	@BeforeEach
	void setUp() throws Exception {
		// Reserved for future per-test setup
	}

	/**
	 * Cleans up test environment after each test method. Currently not used but
	 * available for future expansion.
	 *
	 * @throws Exception if cleanup fails
	 */
	@AfterEach
	void tearDown() throws Exception {
		// Reserved for future per-test cleanup
	}

	/**
	 * Tests that password hashing produces valid, secure results.
	 * 
	 * <p>
	 * Verifies that:
	 * </p>
	 * <ul>
	 * <li>Hashed password is not null</li>
	 * <li>Hashed password is not empty</li>
	 * <li>Hashed password differs from original plain text</li>
	 * </ul>
	 * 
	 * <p>
	 * This ensures basic security properties of the hashing function.
	 * </p>
	 *
	 * @see PasswordUtils#hashPassword(String)
	 */
	@Test
	void testHashPasswordNotNull() {
		String raw = "StrongPass1!"; // كلمة المرور الأصلية قبل التشفير
		String hashed = PasswordUtils.hashPassword(raw); // عملية التشفير

		assertNotNull(hashed, "Hashed password should not be null");
		assertFalse(hashed.isEmpty(), "Hashed password should not be empty");
		assertNotEquals(raw, hashed, "Hashed password should differ from raw password");
	}

	/**
	 * Tests successful password verification with correct credentials.
	 * 
	 * <p>
	 * Verifies that the verification function returns true when the provided plain
	 * text password matches the stored hash.
	 * </p>
	 *
	 * @see PasswordUtils#verifyPassword(String, String)
	 */
	@Test
	void testVerifyPasswordTrue() {
		String raw = "MySecret123";
		String hashed = PasswordUtils.hashPassword(raw);
		assertTrue(PasswordUtils.verifyPassword(raw, hashed),
				"verifyPassword() should return true for matching passwords");
	}

	/**
	 * Tests failed password verification with incorrect credentials.
	 * 
	 * <p>
	 * Verifies that the verification function returns false when the provided plain
	 * text password does not match the stored hash.
	 * </p>
	 * 
	 * <p>
	 * This is crucial for preventing unauthorized access with wrong passwords.
	 * </p>
	 *
	 * @see PasswordUtils#verifyPassword(String, String)
	 */
	@Test
	void testVerifyPasswordFalse() {
		String raw = "MySecret123";
		String hashed = PasswordUtils.hashPassword(raw);

		assertFalse(PasswordUtils.verifyPassword("wrongPass", hashed),
				"verifyPassword() should return false for wrong passwords");
	}

	/**
	 * Tests consistency of password hashing algorithm.
	 * 
	 * <p>
	 * Verifies that hashing the same password multiple times produces identical
	 * results, which is expected behavior when no salt is used.
	 * </p>
	 * 
	 * <p>
	 * <strong>Security Note:</strong> While consistent hashing is useful for
	 * testing, in production environments consider using salted hashes for better
	 * security against rainbow table attacks.
	 * </p>
	 *
	 * @see PasswordUtils#hashPassword(String)
	 */
	@Test
	void testHashConsistency() {
		String raw = "SamePassword";
		String hash1 = PasswordUtils.hashPassword(raw);
		String hash2 = PasswordUtils.hashPassword(raw);

		assertEquals(hash1, hash2, "Hashing same password twice should produce same result since no salt used");
	}
}