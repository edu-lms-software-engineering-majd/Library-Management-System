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
 */
class PasswordUtils_Test {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// Reserved for future global test setup
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		// Reserved for future global test cleanup
	}

	@BeforeEach
	void setUp() throws Exception {
		// Reserved for future per-test setup
	}

	@AfterEach
	void tearDown() throws Exception {
		// Reserved for future per-test cleanup
	}

	@Test
	void testHashPasswordNotNull() {
		String raw = "StrongPass1!";
		String hashed = PasswordUtils.hashPassword(raw);

		assertNotNull(hashed, "Hashed password should not be null");
		assertFalse(hashed.isEmpty(), "Hashed password should not be empty");
		assertNotEquals(raw, hashed, "Hashed password should differ from raw password");
	}

	@Test
	void testVerifyPasswordTrue() {
		String raw = "MySecret123";
		String hashed = PasswordUtils.hashPassword(raw);
		assertTrue(PasswordUtils.verifyPassword(raw, hashed),
				"verifyPassword() should return true for matching passwords");
	}

	@Test
	void testVerifyPasswordFalse() {
		String raw = "MySecret123";
		String hashed = PasswordUtils.hashPassword(raw);
		assertFalse(PasswordUtils.verifyPassword("wrongPass", hashed),
				"verifyPassword() should return false for wrong passwords");
	}

	@Test
	void testHashConsistency() {
		String raw = "SamePassword";
		String hash1 = PasswordUtils.hashPassword(raw);
		String hash2 = PasswordUtils.hashPassword(raw);

		assertEquals(hash1, hash2, "Hashing same password twice should produce same result since no salt used");
	}
}
