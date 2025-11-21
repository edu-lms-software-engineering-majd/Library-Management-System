package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import lms.domain.Role;

class UserDTOTest {
	

	@Test
	void testUserDTO_CreationAndGetters() {
		UUID id = UUID.randomUUID();
		UserDTO dto = new UserDTO(id, "ahmadsalameh", "Ahmad", "Salameh", Role.ADMIN);

		assertEquals(id, dto.userID());
		assertEquals("ahmadsalameh", dto.username());
		assertEquals("Ahmad", dto.firstName());
		assertEquals("Salameh", dto.lastName());
		assertEquals(Role.ADMIN, dto.role());
	}

	@Test
	void testUserDTO_Equality() {
		UUID id = UUID.randomUUID();
		UserDTO dto1 = new UserDTO(id, "ahmad", "Ahmad", "Salameh", Role.MEMBER);
		UserDTO dto2 = new UserDTO(id, "ahmad", "Ahmad", "Salameh", Role.MEMBER);

		assertEquals(dto1, dto2);
		assertEquals(dto1.hashCode(), dto2.hashCode());
	}

	@Test
	void testUserDTO_ToString_NotNull() {
		UserDTO dto = new UserDTO(UUID.randomUUID(), "majd", "Majd", "Awwad", Role.LIBRARIAN);
		assertNotNull(dto.toString());
	}
}
