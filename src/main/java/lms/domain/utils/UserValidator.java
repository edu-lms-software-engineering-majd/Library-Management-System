package lms.domain.utils;

import lms.domain.Role;

/**
 * Validator for User entity fields.
 * 
 * <p>Validates names, email, username, password, and role. Uses singleton pattern.</p>
 * 
 * @author Majd Awwad
 * @version 2.0
 */
public class UserValidator {

	private static final int MIN_PASSWORD_LENGTH = 8;
	private static final UserValidator INSTANCE = new UserValidator();
	
	public UserValidator() {}   

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the validator instance
	 */
	public static UserValidator getInstance() { 
		return INSTANCE; 
	}

	/**
	 * Validates a first name.
	 * 
	 * @param firstName the first name
	 * @throws IllegalArgumentException if invalid
	 */
	public void validateFirstName(String firstName) {
		if (firstName == null || firstName.isBlank()) {
			throw new IllegalArgumentException("First name cannot be empty");
		}
	}

	/**
	 * Validates a last name.
	 * 
	 * @param lastName the last name
	 * @throws IllegalArgumentException if invalid
	 */
	public void validateLastName(String lastName) {
		if (lastName == null || lastName.isBlank()) {
			throw new IllegalArgumentException("Last name cannot be empty");
		}
	}

	/**
	 * Validates an email address.
	 * 
	 * @param email the email
	 * @throws IllegalArgumentException if invalid
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
