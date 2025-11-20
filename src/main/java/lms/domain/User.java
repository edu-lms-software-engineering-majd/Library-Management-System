package lms.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lms.application.UserDTO;
import lms.domain.exception.PasswordReuseException;
import lms.domain.utils.PasswordUtils;
import lms.domain.utils.UserValidator;

/**
 * Domain entity representing a user of the Library Management System.
 * 
 * <p>
 * Manages user identity, authentication, borrowing limits, and notifications.
 * Enforces business rules such as maximum borrow limits and fine restrictions.
 * </p>
 * 
 * @author Majd Awwad
 * @version 2.0
 */
public class User {

	private String firstName;
	private String lastName;
	private String email;
	private final String username; // Immutable after creation
	private String hashedPassword;
	private final UUID userID; // Immutable
	private final LocalDate registrationDate;
	private Role role;
	private List<Loan> loans;
	private Account account;

	private static final int MAX_BORROW_LIMIT = 10;
	private List<Notification> unreadNotifications = new ArrayList<>();
	private List<Notification> readNotifications = new ArrayList<>();

	/**
	 * Creates a new User with validation.
	 * 
	 * @param firstName the user's first name
	 * @param lastName the user's last name
	 * @param email the user's email address
	 * @param username the unique username (immutable)
	 * @param hashedPassword the hashed password
	 * @param role the user's role (ADMIN or MEMBER)
	 * @throws IllegalArgumentException if any field fails validation
	 */
	public User(String firstName, String lastName, String email, String username, String hashedPassword, Role role) {
		// Validate all inputs using existing validator
		UserValidator validator = UserValidator.getInstance();
		validator.validateFirstName(firstName);
		validator.validateLastName(lastName);
		validator.validateEmail(email);
		validator.validateUsername(username);
		validator.validateHashedPassword(hashedPassword);
		validator.validateRole(role);

		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.username = username;
		this.hashedPassword = hashedPassword;
		this.role = role;

		this.registrationDate = LocalDate.now();
		this.userID = UUID.randomUUID();

		this.unreadNotifications = new ArrayList<>();
		this.readNotifications = new ArrayList<>();
		this.loans = new ArrayList<>(User.MAX_BORROW_LIMIT);
		this.account = new Account(this.userID);

	}

	public User(String firstName, String lastName, String email, String username, String hashedPassword, Role role,
			List<Loan> loans, Account account) {
		this(firstName, lastName, email, username, hashedPassword, role);
		this.loans = loans;
		this.account = account;
	}

	public boolean verifyPassword(String rawPassword) {
		return PasswordUtils.verifyPassword(rawPassword, this.hashedPassword);
	}

	public UserDTO toDTO() {
		return new UserDTO(this.userID, this.username, this.firstName, this.lastName, this.role);
	}

	public void changePassword(String newPassword) throws IllegalArgumentException, PasswordReuseException {
		if (newPassword == null || newPassword.length() < 8) {
			throw new IllegalArgumentException("Password too weak.");
		}
		if (this.verifyPassword(newPassword)) {
			throw new PasswordReuseException("New password cannot be the same as the old password.");
		}
		this.hashedPassword = PasswordUtils.hashPassword(newPassword);
	}

	public void changeRole(Role newRole) {
		if (newRole == null)
			throw new IllegalArgumentException("Role cannot be null");
		this.role = newRole;
	}

	/**
	 * Updates the user's email address with validation.
	 * 
	 * @param newEmail the new email address
	 * @throws IllegalArgumentException if email format is invalid
	 */
	public void changeEmail(String newEmail) {
		UserValidator.getInstance().validateEmail(newEmail);
		this.email = newEmail;
	}

	/**
	 * Updates the user's name information.
	 * 
	 * @param newFirstName the new first name
	 * @param newLastName the new last name
	 * @throws IllegalArgumentException if names are invalid
	 */
	public void updateName(String newFirstName, String newLastName) {
		UserValidator validator = UserValidator.getInstance();
		validator.validateFirstName(newFirstName);
		validator.validateLastName(newLastName);
		this.firstName = newFirstName;
		this.lastName = newLastName;
	}

	public boolean hasFine() {
		return account.getTotalFines() > 0;
	}

	public boolean canBorrow() {
		return loans.size() < MAX_BORROW_LIMIT && !hasFine();
	}

	public void addLoan(Loan loan) {
		this.loans.add(loan);
	}

	public void removeLoan(Loan loan) {
		this.loans.remove(loan);
	}

	public void addNotification(Notification notification) {
		this.unreadNotifications.add(notification);
	}

	public void markAsRead(Notification notification) {
		if (unreadNotifications.remove(notification)) {
			readNotifications.add(notification);
		}
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	// NOTE: Username is immutable after creation - no setter provided

	public UUID getUserID() {
		return userID;
	}

	public List<Loan> getLoans() {
		return Collections.unmodifiableList(loans);
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public Role getRole() {
		return role;
	}

	public Account getAccount() {
		return account;
	}

	public List<Notification> getUnreadNotifications() {
		return Collections.unmodifiableList(unreadNotifications);
	}

	public List<Notification> getReadNotifications() {
		return Collections.unmodifiableList(readNotifications);
	}
}
