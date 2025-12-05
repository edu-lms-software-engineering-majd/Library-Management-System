package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
	private UserDTO adminDTO;
	private UserDTO memberDTO;

	@BeforeEach
	void setup() {
		userService = new UserService(userRepo);

		adminDTO = new UserDTO(UUID.randomUUID(), "ahmad", "Ahmad", "Salameh", "ahmad@example.com", Role.ADMIN);
		memberDTO = new UserDTO(UUID.randomUUID(), "majd", "Majd", "Awwad", "majd@example.com", Role.MEMBER);
	}

	// ==========================================================================
	// GET ALL USERS TESTS
	// ==========================================================================
	@Nested
	@DisplayName("Get All Users Tests")
	class GetAllUsersTests {

		@Test
		@DisplayName("Should return all users successfully")
		void shouldReturnAllUsers() {
			User u1 = mock(User.class);
			User u2 = mock(User.class);

			when(u1.toDTO()).thenReturn(memberDTO);
			when(u2.toDTO()).thenReturn(adminDTO);

			when(userRepo.getAllUsers()).thenReturn(List.of(u1, u2));

			List<UserDTO> result = userService.getAllUsers();

			assertEquals(2, result.size());
			verify(userRepo).getAllUsers();
		}
	}

	// ==========================================================================
	// GET USER BY USERNAME TESTS
	// ==========================================================================
	@Nested
	@DisplayName("Get User By Username Tests")
	class GetUserByUsernameTests {

		@Test
		@DisplayName("Should return user when username exists")
		void shouldReturnUserWhenExists() throws Exception {
			User mockUser = mock(User.class);
			when(mockUser.toDTO()).thenReturn(memberDTO);
			when(userRepo.getByUserName("majd")).thenReturn(Optional.of(mockUser));

			UserDTO result = userService.getUserByUsername("majd");

			assertNotNull(result);
			assertEquals("majd", result.username());
		}

		@Test
		@DisplayName("Should throw when user not found")
		void shouldThrowWhenUserNotFound() {
			when(userRepo.getByUserName("x")).thenReturn(Optional.empty());

			assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("x"));
		}
	}

	// ==========================================================================
	// UPDATE USER TESTS
	// ==========================================================================
	@Nested
	@DisplayName("Update User Tests")
	class UpdateUserTests {

		@Test
		@DisplayName("Should update user successfully")
		void shouldUpdateUserSuccessfully() throws Exception {
			UUID id = memberDTO.userID();
			User mockUser = mock(User.class);

			when(userRepo.getByID(id)).thenReturn(Optional.of(mockUser));
			when(userRepo.update(mockUser)).thenReturn(true);

			boolean result = userService.updateUser(memberDTO, id, null, "new@mail.com", "pass12345", Role.ADMIN);

			assertTrue(result);
			verify(mockUser).changeEmail("new@mail.com");
			verify(mockUser).changePassword("pass12345");
			verify(mockUser).changeRole(Role.ADMIN);
			verify(userRepo).update(mockUser);
		}

		@Test
		@DisplayName("Should throw when updating non-existing user")
		void shouldThrowWhenTargetUserMissing() {
			UUID id = UUID.randomUUID();
			when(userRepo.getByID(id)).thenReturn(Optional.empty());

			assertThrows(UserNotFoundException.class,
					() -> userService.updateUser(adminDTO, id, null, "a", "b", Role.ADMIN));
		}

		@Test
		@DisplayName("Should reject username change")
		void shouldRejectUsernameChange() {
			UUID id = memberDTO.userID();
			User mockUser = mock(User.class);
			when(userRepo.getByID(id)).thenReturn(Optional.of(mockUser));

			assertThrows(IllegalArgumentException.class,
					() -> userService.updateUser(memberDTO, id, "newUsername", null, null, null));
		}

		@Test
		@DisplayName("Should reject unauthorized update")
		void shouldRejectUnauthorizedUser() {
			UUID otherId = UUID.randomUUID();

			assertThrows(IllegalAccessException.class,
					() -> userService.updateUser(memberDTO, otherId, null, "email", "pass", Role.ADMIN));
		}
	}

	// ==========================================================================
	// DELETE USER TESTS
	// ==========================================================================
	@Nested
	@DisplayName("Delete User Tests")
	class DeleteUserTests {

		@Test
		@DisplayName("Should delete user successfully")
		void shouldDeleteUserSuccessfully() throws Exception {
			when(userRepo.delete("majd")).thenReturn(true);

			boolean result = userService.deleteByUsername("majd");

			assertTrue(result);
			verify(userRepo).delete("majd");
		}

		@Test
		@DisplayName("Should throw when username not found")
		void shouldThrowWhenUsernameNotFound() throws Exception {
			when(userRepo.delete("unknown")).thenThrow(new UserNotFoundException("not found"));

			assertThrows(UserNotFoundException.class, () -> userService.deleteByUsername("unknown"));
		}
	}

	// ==========================================================================
	// CAN BORROW TESTS
	// ==========================================================================
	@Nested
	@DisplayName("CanBorrow Tests")
	class CanBorrowTests {

		@Test
		@DisplayName("Should return true when user can borrow")
		void shouldReturnTrueWhenUserCanBorrow() throws Exception {
			User mockUser = mock(User.class);
			UUID id = memberDTO.userID();

			when(userRepo.getByID(id)).thenReturn(Optional.of(mockUser));
			when(mockUser.canBorrow()).thenReturn(true);

			assertTrue(userService.canBorrow(id));
		}

		@Test
		@DisplayName("Should return false when user cannot borrow")
		void shouldReturnFalseWhenUserCannotBorrow() throws Exception {
			User mockUser = mock(User.class);
			UUID id = memberDTO.userID();

			when(userRepo.getByID(id)).thenReturn(Optional.of(mockUser));
			when(mockUser.canBorrow()).thenReturn(false);

			assertFalse(userService.canBorrow(id));
		}

		@Test
		@DisplayName("Should throw when user not found")
		void shouldThrowUserNotFound() {
			UUID id = UUID.randomUUID();
			when(userRepo.getByID(id)).thenReturn(Optional.empty());

			assertThrows(UserNotFoundException.class, () -> userService.canBorrow(id));
		}
	}
}
