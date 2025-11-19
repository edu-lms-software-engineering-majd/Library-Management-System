package lms.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

		this.loans = new ArrayList<>(MAX_BORROW_LIMIT);
		this.account = new Account(this.userID);
		this.unreadNotifications = new ArrayList<>();
		this.readNotifications = new ArrayList<>();
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public UUID getUserID() {
		return userID;
	}

	public String getUsername() {
		return username;
	}

	public boolean hasFine() {
		return account.getTotalFines() > 0;
	}

	public boolean canBorrow() {
		return loans.size() < MAX_BORROW_LIMIT && !hasFine();
	}

	public void addLoan(Loan loan) {
		loans.add(loan);
	}

	public void removeLoan(Loan loan) {
		loans.remove(loan);
	}

	public void addNotification(Notification notification) {
		unreadNotifications.add(notification);
	}

	public void markAsRead(Notification notification) {
		if (unreadNotifications.remove(notification)) {
			readNotifications.add(notification);
		}
	}

	public List<Loan> getLoans() {
		return Collections.unmodifiableList(loans);
	}

	public List<Notification> getUnreadNotifications() {
		return Collections.unmodifiableList(unreadNotifications);
	}

	public List<Notification> getReadNotifications() {
		return Collections.unmodifiableList(readNotifications);
	}
}