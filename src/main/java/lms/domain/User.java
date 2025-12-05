package lms.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	private final String username;  
	private String hashedPassword;
	private final UUID userID;  
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
	 * @param firstName      the user's first name
	 * @param lastName       the user's last name
	 * @param email          the user's email address
	 * @param username       the unique username (immutable)
	 * @param hashedPassword the hashed password
	 * @param role           the user's role (ADMIN or MEMBER)
	 * @throws IllegalArgumentException if any field fails validation
	 */
	public User(String firstName, String lastName, String email, String username, String hashedPassword, Role role) {
		 
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

	/**
	 * Gets the user's account.
	 * 
	 * @return the user's account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * Gets the user's unique identifier.
	 * 
	 * @return the user's UUID
	 */
	public UUID getUserID() {
		return userID;
	}

	/**
	 * Gets the user's registration date.
	 * 
	 * @return the registration date
	 */
	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	/**
	 * Gets the user's role.
	 * 
	 * @return the user's role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * Converts this user to a Data Transfer Object for presentation layer.
	 * 
	 * @return a UserDTO containing essential user information
	 */
	public UserDTO toDTO() {
		return new UserDTO(this.userID, this.username, this.firstName, this.lastName, this.email, this.role);
	}

	/**
	 * Verifies if the provided password matches the user's stored password.
	 * 
	 * @param rawPassword the raw password to verify
	 * @return {@code true} if the password matches, {@code false} otherwise
	 */
	public boolean verifyPassword(String rawPassword) {
		return PasswordUtils.verifyPassword(rawPassword, this.hashedPassword);
	}

	/**
	 * Changes the user's password with validation.
	 * 
	 * <p>
	 * Enforces password security rules:
	 * <ul>
	 * <li>Password must be at least 8 characters long</li>
	 * <li>New password cannot be the same as the old password</li>
	 * </ul>
	 * </p>
	 * 
	 * @param newPassword the new password to set
	 * @throws IllegalArgumentException if password is too weak
	 * @throws PasswordReuseException   if new password matches old password
	 */
	public void changePassword(String newPassword) throws IllegalArgumentException, PasswordReuseException {

		if (newPassword == null || newPassword.length() < 8) {
			throw new IllegalArgumentException("Password too weak.");
		}

		if (this.verifyPassword(newPassword)) {
			throw new PasswordReuseException("New password cannot be the same as the old password.");
		}
		this.hashedPassword = PasswordUtils.hashPassword(newPassword);
	}

	/**
	 * Changes the user's role.
	 * 
	 * <p>
	 * This is typically called by administrators to grant or revoke privileges.
	 * </p>
	 * 
	 * @param newRole the new role to assign
	 * @throws IllegalArgumentException if role is null
	 */
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
	 * @param newLastName  the new last name
	 * @throws IllegalArgumentException if names are invalid
	 */
	public void updateName(String newFirstName, String newLastName) {
		UserValidator validator = UserValidator.getInstance();
		validator.validateFirstName(newFirstName);
		validator.validateLastName(newLastName);
		this.firstName = newFirstName;
		this.lastName = newLastName;
	}

	/**
	 * Checks if the user has any outstanding fines.
	 * 
	 * @return {@code true} if the user has fines, {@code false} otherwise
	 */
	public boolean hasFine() {
		return account.getTotalFines() > 0;
	}

	/**
	 * Checks if the user can borrow items.
	 * 
	 * <p>
	 * Business rules:
	 * <ul>
	 * <li>User must not exceed the maximum borrow limit ({@value #MAX_BORROW_LIMIT}
	 * items)</li>
	 * <li>User must not have any outstanding fines</li>
	 * </ul>
	 * </p>
	 * 
	 * @return {@code true} if the user can borrow, {@code false} otherwise
	 */
	public boolean canBorrow() {
		return loans.size() < MAX_BORROW_LIMIT && !hasFine();
	}

	/**
	 * Adds a loan to the user's active loans.
	 * 
	 * <p>
	 * This method should only be called after verifying {@link #canBorrow()}.
	 * </p>
	 * 
	 * @param loan the loan to add
	 * @throws IllegalArgumentException if loan is null
	 */
	public void addLoan(Loan loan) {
		if (loan == null) {
			throw new IllegalArgumentException("Loan cannot be null");
		}
		loans.add(loan);
	}

	/**
	 * Removes a loan from the user's active loans.
	 * 
	 * <p>
	 * Called when a user returns an item.
	 * </p>
	 * 
	 * @param loan the loan to remove
	 */
	public void removeLoan(Loan loan) {
		loans.remove(loan);
	}

	/**
	 * Adds a notification to the user's unread notifications.
	 * 
	 * @param notification the notification to add
	 * @throws IllegalArgumentException if notification is null
	 */
	public void addNotification(Notification notification) {
		if (notification == null) {
			throw new IllegalArgumentException("Notification cannot be null");
		}
		unreadNotifications.add(notification);
	}

	/**
	 * Marks a notification as read, moving it from unread to read list.
	 * 
	 * @param notification the notification to mark as read
	 */
	public void markAsRead(Notification notification) {
		if (unreadNotifications.remove(notification)) {
			readNotifications.add(notification);
		}
	}

	/**
	 * Gets the user's first name.
	 * 
	 * @return the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Gets the user's last name.
	 * 
	 * @return the last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Gets the user's full name.
	 * 
	 * @return the full name (first name + last name)
	 */
	public String getFullName() {
		return firstName + " " + lastName;
	}

	/**
	 * Gets the user's email address.
	 * 
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Gets the user's username.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets an immutable view of the user's active loans.
	 * 
	 * @return an unmodifiable list of loans
	 */
	public List<Loan> getLoans() {
		return Collections.unmodifiableList(loans);
	}

	/**
	 * Gets the number of active loans.
	 * 
	 * @return the number of active loans
	 */
	public int getActiveLoanCount() {
		return loans.size();
	}

	/**
	 * Checks if the user has reached the maximum borrow limit.
	 * 
	 * @return {@code true} if at max capacity, {@code false} otherwise
	 */
	public boolean isAtBorrowLimit() {
		return loans.size() >= MAX_BORROW_LIMIT;
	}

	/**
	 * Gets an immutable view of the user's unread notifications.
	 * 
	 * @return an unmodifiable list of unread notifications
	 */
	public List<Notification> getUnreadNotifications() {
		return Collections.unmodifiableList(unreadNotifications);
	}

	/**
	 * Gets an immutable view of the user's read notifications.
	 * 
	 * @return an unmodifiable list of read notifications
	 */
	public List<Notification> getReadNotifications() {
		return Collections.unmodifiableList(readNotifications);
	}

	/**
	 * Gets the total count of unread notifications.
	 * 
	 * @return the number of unread notifications
	 */
	public int getUnreadNotificationCount() {
		return unreadNotifications.size();
	}

	/**
	 * Checks if the user has any unread notifications.
	 * 
	 * @return {@code true} if there are unread notifications, {@code false}
	 *         otherwise
	 */
	public boolean hasUnreadNotifications() {
		return !unreadNotifications.isEmpty();
	}

	public List<Notification> getNotifications() {
		return Collections.unmodifiableList(unreadNotifications);
	}


	public void setEmail(String email) {
		changeEmail(email); // Uses your validation
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


}
