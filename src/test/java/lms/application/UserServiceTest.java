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

		adminDTO = new UserDTO(UUID.randomUUID(), "ahmad", "Ahmad", "Salameh", "ahmad@test.com", Role.ADMIN);

		memberDTO = new UserDTO(UUID.randomUUID(), "majd", "Majd", "Awwad", "majd@test.com", Role.MEMBER);
	}

	@Test
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

	@Test
	void shouldReturnUserWhenExists() throws Exception {
		User mockUser = mock(User.class);
		when(mockUser.toDTO()).thenReturn(memberDTO);
		when(userRepo.getByUserName("majd")).thenReturn(Optional.of(mockUser));

		UserDTO result = userService.getUserByUsername("majd");

		assertNotNull(result);
		assertEquals("majd", result.username());
	}

	@Test
	void shouldThrowWhenUserNotFound() {
		when(userRepo.getByUserName("x")).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("x"));
	}

	@Test
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
	void shouldThrowWhenTargetUserMissing() {
		UUID id = UUID.randomUUID();
		when(userRepo.getByID(id)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class,
				() -> userService.updateUser(adminDTO, id, null, "a", "b", Role.ADMIN));
	}

	@Test
	void shouldRejectUsernameChange() {
		UUID id = memberDTO.userID();
		User mockUser = mock(User.class);
		when(userRepo.getByID(id)).thenReturn(Optional.of(mockUser));

		assertThrows(IllegalArgumentException.class,
				() -> userService.updateUser(memberDTO, id, "newUsername", null, null, null));
	}

	@Test
	void shouldRejectUnauthorizedUser() {
		UUID otherId = UUID.randomUUID();

		assertThrows(IllegalAccessException.class,
				() -> userService.updateUser(memberDTO, otherId, null, "email", "pass", Role.ADMIN));
	}

	@Test
	void shouldDeleteUserSuccessfully() throws Exception {
		when(userRepo.delete("majd")).thenReturn(true);

		boolean result = userService.deleteByUsername("majd");

		assertTrue(result);
		verify(userRepo).delete("majd");
	}

	@Test
	void shouldThrowWhenUsernameNotFound() throws Exception {
		when(userRepo.delete("unknown")).thenThrow(new UserNotFoundException("not found"));

		assertThrows(UserNotFoundException.class, () -> userService.deleteByUsername("unknown"));
	}

	@Test
	void shouldReturnTrueWhenUserCanBorrow() throws Exception {
		User mockUser = mock(User.class);
		UUID id = memberDTO.userID();

		when(userRepo.getByID(id)).thenReturn(Optional.of(mockUser));
		when(mockUser.canBorrow()).thenReturn(true);

		assertTrue(userService.canBorrow(id));
	}

	@Test
	void shouldReturnFalseWhenUserCannotBorrow() throws Exception {
		User mockUser = mock(User.class);
		UUID id = memberDTO.userID();

		when(userRepo.getByID(id)).thenReturn(Optional.of(mockUser));
		when(mockUser.canBorrow()).thenReturn(false);

		assertFalse(userService.canBorrow(id));
	}

	@Test
	void shouldThrowUserNotFound() {
		UUID id = UUID.randomUUID();
		when(userRepo.getByID(id)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.canBorrow(id));
	}
}
