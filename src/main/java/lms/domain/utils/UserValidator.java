package lms.domain.utils;

import lms.domain.Role;

/**
 * Validator class for User entity fields and operations.
 * 
 * <p>
 * Provides validation methods for user-related data such as names, email,
 * username, password, and role to ensure data integrity and business rules are
 * enforced.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class UserValidator {

	private static final int MIN_PASSWORD_LENGTH = 8;

	/**
	 * Validates a user's first name.
	 * 
	 * @param firstName the first name to validate
	 * @throws IllegalArgumentException if first name is null or blank
	 */
	public void validateFirstName(String firstName) {
		if (firstName == null || firstName.isBlank()) {
			throw new IllegalArgumentException("First name cannot be empty");
		}
	}

	/**
	 * Validates a user's last name.
	 * 
	 * @param lastName the last name to validate
	 * @throws IllegalArgumentException if last name is null or blank
	 */
	public void validateLastName(String lastName) {
		if (lastName == null || lastName.isBlank()) {
			throw new IllegalArgumentException("Last name cannot be empty");
		}
	}

	/**
	 * Validates an email address.
	 * 
	 * @param email the email to validate
	 * @throws IllegalArgumentException if email is null or doesn't contain "@"
	 */
	public void validateEmail(String email) {
		if (email == null || !email.contains("@")) {
			throw new IllegalArgumentException("Invalid email");
		}
	}

	/**
	 * Validates a username.
	 * 
	 * @param username the username to validate
	 * @throws IllegalArgumentException if username is null or blank
	 */
	public void validateUsername(String username) {
		if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("Username cannot be empty");
		}
	}

	/**
	 * Validates a hashed password.
	 * 
	 * @param hashedPassword the hashed password to validate
	 * @throws IllegalArgumentException if hashed password is null or blank
	 */
	public void validateHashedPassword(String hashedPassword) {
		if (hashedPassword == null || hashedPassword.isBlank()) {
			throw new IllegalArgumentException("Hashed password cannot be empty");
		}
	}

	/**
	 * Validates a user role.
	 * 
	 * @param role the role to validate
	 * @throws IllegalArgumentException if role is null
	 */
	public void validateRole(Role role) {
		if (role == null) {
			throw new IllegalArgumentException("Role cannot be null");
		}
	}

	/**
	 * Validates a new password before changing.
	 * 
	 * @param newPassword the new password to validate
	 * @throws IllegalArgumentException if password is null or too short
	 */
	public void validateNewPassword(String newPassword) {
		if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
			throw new IllegalArgumentException("Password too weak.");
		}
	}

	/**
	 * Validates all user fields at once during user creation.
	 * 
	 * @param firstName      the user's first name
	 * @param lastName       the user's last name
	 * @param email          the user's email
	 * @param username       the user's username
	 * @param hashedPassword the user's hashed password
	 * @param role           the user's role
	 * @throws IllegalArgumentException if any field fails validation
	 */
	public void validateUserFields(String firstName, String lastName, String email, String username,
			String hashedPassword, Role role) {
		validateFirstName(firstName);
		validateLastName(lastName);
		validateEmail(email);
		validateUsername(username);
		validateHashedPassword(hashedPassword);
		validateRole(role);
	}
}
