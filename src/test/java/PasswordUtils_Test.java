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

class PasswordUtils_Test {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testHashPasswordNotNull()

	{
		String raw = "StrongPass1!"; // هان انا بقصد اني اكتب كلمة المرو قبل عملية التشفير
		String hashed = PasswordUtils.hashPassword(raw);// حول كلمة المرور الي تشفير تمام هيك

		assertNotNull(hashed, "Hashed password should not be null");
		assertFalse(hashed.isEmpty(), "Hashed password should not be empty");// false
		assertNotEquals(raw, hashed, "Hashed password should differ from raw password");// هان مختلفات
	}

	@Test
	void testVerifyPasswordTrue() {
		String raw = "MySecret123";
		String hashed = PasswordUtils.hashPassword(raw);
		assertTrue(PasswordUtils.verifyPassword(raw, hashed),
				"verifyPassword() should return true for matching passwords");
	}

	@Test
	void testVerifyPasswordFalse()

	{
		String raw = "MySecret123";
		String hashed = PasswordUtils.hashPassword(raw);

		assertFalse(PasswordUtils.verifyPassword("wrongPass", hashed),
				"verifyPassword() should return false for wrong passwords");
	}

	@Test
	void testHashConsistency()

	{
		String raw = "SamePassword";
		String hash1 = PasswordUtils.hashPassword(raw);
		String hash2 = PasswordUtils.hashPassword(raw);

		assertEquals(hash1, hash2, "Hashing same password twice should produce same result since no salt used");
	}

}
