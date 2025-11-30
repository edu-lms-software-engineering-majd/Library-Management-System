package lms.domain;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoleTest {

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
	void testEnumValuesExist() {
		assertNotNull(Role.valueOf("MEMBER"));
		assertNotNull(Role.valueOf("ADMIN"));
		assertNotNull(Role.valueOf("LIBRARIAN"));
	}

	/**
	 * Tests that the correct number of enum values are defined.
	 * 
	 * <p>
	 * Verifies that exactly three roles are defined in the system, preventing
	 * accidental additions or removals that could break role-based authorization
	 * logic.
	 * </p>
	 *
	 * @see Role#values()
	 */
	@Test
	void testEnumCount() {
		Role[] roles = Role.values();
		assertEquals(3, roles.length);
	}

	/**
	 * Tests the string representation of enum values.
	 * 
	 * <p>
	 * Verifies that each role returns the expected string name when
	 * {@link Object#toString()} is called, ensuring consistency in logging,
	 * serialization, and UI display.
	 * </p>
	 *
	 * @see Object#toString()
	 */
	@Test
	void testEnumNames() {
		assertEquals("MEMBER", Role.MEMBER.toString());
		assertEquals("ADMIN", Role.ADMIN.toString());
		assertEquals("LIBRARIAN", Role.LIBRARIAN.toString());
	}

	/**
	 * Tests that all enum values are distinct and unique.
	 * 
	 * <p>
	 * Verifies that no two roles are considered equal, ensuring that role-based
	 * access control can reliably distinguish between different permission levels.
	 * </p>
	 */
	@Test
	void testEnumDistinctness() {
		assertNotEquals(Role.ADMIN, Role.MEMBER);
		assertNotEquals(Role.MEMBER, Role.LIBRARIAN);
		assertNotEquals(Role.ADMIN, Role.LIBRARIAN);
	}
}
