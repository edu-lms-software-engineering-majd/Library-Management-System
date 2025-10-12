import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.Role;

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

     
    @Test
    void testEnumCount() {
        Role[] roles = Role.values();
        assertEquals(3, roles.length);
    }

    
    @Test
    void testEnumNames() {
        assertEquals("MEMBER", Role.MEMBER.toString());
        assertEquals("ADMIN", Role.ADMIN.toString());
        assertEquals("LIBRARIAN", Role.LIBRARIAN.toString());
    }

    @Test
	void testEnumDistinctness() {
        assertNotEquals(Role.ADMIN, Role.MEMBER);
        assertNotEquals(Role.MEMBER, Role.LIBRARIAN);
        assertNotEquals(Role.ADMIN, Role.LIBRARIAN);
    }

    
}
