package lms.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lms.application.UserDTO;
import lms.domain.exception.PasswordReuseException;
import lms.domain.utils.PasswordUtils;

/**
 * Domain entity representing a user of the Library Management System.
 */
public class User {

	private String firstName;
	private String lastName;
	private String email;
	private String username;
	private String hashedPassword;
	private UUID userID;
	private final LocalDate registrationDate;
	private Role role;
	private List<Loan> loans;
	private Account account;

	private static final int MAX_BORROW_LIMIT = 10;
	private List<Notification> unreadNotifications = new ArrayList<>();
	private List<Notification> readNotifications = new ArrayList<>();

	public User(String firstName, String lastName, String email, String username, String hashedPassword, Role role) {
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

	public void changeEmail(String newEmail) {
		if (newEmail == null || !newEmail.contains("@"))
			throw new IllegalArgumentException("Invalid email");
		this.email = newEmail;
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

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

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
