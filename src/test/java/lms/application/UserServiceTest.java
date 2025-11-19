package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.domain.Role;
import lms.domain.User;
import lms.domain.UserRepository;
import lms.domain.exception.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepo;

	private UserService userService;
	private User testUser;
	private UserDTO testAdminDTO;
	private UserDTO testMemberDTO;
	private UUID testUserId;

	@BeforeEach
	void setUp() {
		userService = new UserService(userRepo);
		testUserId = UUID.randomUUID();
		testUser = new User("Majd", "Awwad", "majd@example.com", "majd", "hashedpass", Role.MEMBER);
		testAdminDTO = new UserDTO(UUID.randomUUID(), "admin", "Ahmad", "Salameh", Role.ADMIN);
		testMemberDTO = new UserDTO(UUID.randomUUID(), "user", "Majd", "Awwad", Role.MEMBER);
	}

	// ✅ Test 1: getAllUsers returns mapped list of DTOs
	@Test
	void whenGetAllUsers_thenReturnListOfUserDTOs() {
		when(userRepo.getAllUsers()).thenReturn(List.of(testUser));

		var result = userService.getAllUsers();

		assertEquals(1, result.size());
		assertEquals(testUser.getUsername(), result.get(0).username());
		verify(userRepo).getAllUsers();
	}

	// ✅ Test 2: getUserByUsername returns correct user
	@Test
	void givenExistingUser_whenGetUserByUsername_thenReturnUserDTO() throws Exception {
		when(userRepo.getByUserName("majd")).thenReturn(Optional.of(testUser));

		UserDTO result = userService.getUserByUsername("majd");

		assertNotNull(result);
		assertEquals("majd", result.username());
	}

	// ✅ Test 3: getUserByUsername throws when not found
	@Test
	void givenNonExistingUser_whenGetUserByUsername_thenThrowsException() {
		when(userRepo.getByUserName("unknown")).thenReturn(Optional.empty());
		assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("unknown"));
	}

	// ✅ Test 4: updateUser succeeds for admin updating self
	@Test
	void givenAdminUpdatingSelf_whenUpdateUser_thenSuccess() throws Exception {
		UUID adminId = testAdminDTO.userID();
		User adminUser = new User("Ahmad", "Salameh", "admin@example.com", "admin", "hash", Role.ADMIN);

		when(userRepo.getByID(adminId)).thenReturn(Optional.of(adminUser));
		when(userRepo.update(any(User.class))).thenReturn(true);

		boolean result = userService.updateUser(testAdminDTO, adminId, "newAdmin", null, null, Role.ADMIN);

		assertTrue(result);
		verify(userRepo).update(any(User.class));
	}

	// ✅ Test 5: updateUser throws if not authorized
	@Test
	void givenNonAdminUpdatingOtherUser_whenUpdateUser_thenThrowsIllegalAccess() {
		assertThrows(IllegalAccessException.class,
				() -> userService.updateUser(testMemberDTO, testUserId, "newName", null, null, null));
	}

	// ✅ Test 6: updateUser throws if user not found
	@Test
	void givenAdminAndMissingUser_whenUpdateUser_thenThrowsUserNotFound() {
		when(userRepo.getByID(testAdminDTO.userID())).thenReturn(Optional.empty());
		assertThrows(UserNotFoundException.class,
				() -> userService.updateUser(testAdminDTO, testAdminDTO.userID(), "new", null, null, null));
	}

	// ✅ Test 7: deleteUserByUsername deletes correctly
	@Test
	void givenExistingUsername_whenDeleteUser_thenRepositoryCalled() throws Exception {
		when(userRepo.delete("majd")).thenReturn(true);

		boolean result = userService.deleteUserByUsername("majd");

		assertTrue(result);
		verify(userRepo).delete("majd");
	}

	// ✅ Test 8: canBorrow returns true if user eligible
	@Test
	void givenValidUser_whenCanBorrow_thenReturnsTrue() throws Exception {
		when(userRepo.getByID(testUserId)).thenReturn(Optional.of(testUser));
		boolean result = userService.canBorrow(testUserId);
		assertTrue(result);
		verify(userRepo).getByID(testUserId);
	}

	// ✅ Test 9: canBorrow throws if user not found
	@Test
	void givenInvalidUser_whenCanBorrow_thenThrowsException() {
		when(userRepo.getByID(testUserId)).thenReturn(Optional.empty());
		assertThrows(UserNotFoundException.class, () -> userService.canBorrow(testUserId));
	}
}
