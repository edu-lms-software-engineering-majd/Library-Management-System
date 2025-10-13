package lms.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lms.application.UserDTO;
import lms.domain.utils.PasswordUtils;

/**
 * Domain entity representing a user of the Library Management System.
 *
 * <p>
 * A {@code User} encapsulates personal information, authentication credentials,
 * role-based permissions, borrowing activity, and a financial account. It
 * enforces its own invariants, such as password strength, email validity, and
 * non-null roles, ensuring integrity of user data within the domain model.
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 * <li>Maintain user identity (UUID, username, registration date).</li>
 * <li>Store and verify secure hashed passwords.</li>
 * <li>Track assigned role ({@link Role}) for authorization decisions.</li>
 * <li>Provide access to borrowing records ({@link Loan}) in a read-only
 * fashion.</li>
 * <li>Manage account-related information for fines and payments
 * ({@link Account}).</li>
 * <li>Offer controlled mutations such as changing password, email, or role,
 * with validation rules enforced inside the entity.</li>
 * </ul>
 *
 * <h2>Design Notes</h2>
 * <ul>
 * <li>This class belongs to the <b>domain layer</b> and should not contain
 * persistence or presentation logic.</li>
 * <li>External systems interact with {@code User} via immutable {@link UserDTO}
 * objects when exposing user data across boundaries.</li>
 * <li>Password operations delegate to {@link PasswordUtils} for hashing and
 * verification.</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * 
 * <pre>
 * User user = new User("Majd", "Awwad", "majd@gmail.com", "majdawwad", PasswordUtils.hashPassword("StrongPass1!"),
 * 		Role.ADMIN);
 *
 * boolean ok = user.verifyPassword("StrongPass1!"); // true
 * user.changePassword("AnotherPass2!");
 * user.changeEmail("majdawwad@gmail.com");
 * </pre>
 *
 * @author Majd Awwad
 * @version 2.0
 */
public class User {

	/** User's first name */
	private String firstName;

	/** User's last name */
	private String lastName;

	/** User's email address */
	private String email;

	/** User's login username (unique within the system). */
	private String username;

	/** User's password stored as a hash */
	private String hashedPassword;

	/** Globally unique identifier for the user. */
	private UUID userID;

	/** Date when the user registered */
	private final LocalDate registrationDate;

	/** User's role (e.g., ADMIN, MEMBER) */
	private Role role;

	/** List of items the user has borrowed */
	private List<Loan> loans;

	/** User's financial account for fines and payments */
	private Account account;

	/**
	 * Constructs a new user with the given personal details, hashed password, and
	 * role.
	 * <p>
	 * The user ID is generated automatically, registration date is set to now,
	 * loans start empty, and a fresh {@link Account} is created.
	 * </p>
	 *
	 * @param firstName      first name of the user
	 * @param lastName       last name of the user
	 * @param email          user’s email (must be valid format)
	 * @param username       login username (must be unique)
	 * @param hashedPassword securely hashed password
	 * @param role           user role (cannot be {@code null})
	 */

	public User(String firstName, String lastName, String email, String username, String hashedPassword, Role role) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.username = username;
		this.hashedPassword = hashedPassword;
		this.role = role;

		this.registrationDate = LocalDate.now();
		this.userID = UUID.randomUUID();

		this.loans = new ArrayList<>();
		this.account = new Account();

	}

	/**
	 * Constructs a new user with a preexisting set of loans and account. Typically
	 * used when restoring users from persistence.
	 *
	 * @param firstName      first name
	 * @param lastName       last name
	 * @param email          email address
	 * @param username       login username
	 * @param hashedPassword hashed password
	 * @param role           user role
	 * @param loans          existing loans
	 * @param account        existing financial account
	 */

	public User(String firstName, String lastName, String email, String username, String hashedPassword, Role role,
			List<Loan> loans, Account account) {
		this(firstName, lastName, email, username, hashedPassword, role);
		this.loans = loans;
		this.account = account;
	}

	/**
	 * 
	 * Verifies whether the given raw password matches the stored hashed password.
	 *
	 * @param rawPassword plain text password
	 * @return {@code true} if valid, {@code false} otherwise
	 */

	public boolean verifyPassword(String rawPassword) {
		return PasswordUtils.verifyPassword(rawPassword, this.hashedPassword);
	}

	/** Converts this domain entity into an immutable {@link UserDTO}. */

	public UserDTO toDTO() {
		return new UserDTO(this.userID, this.username, this.firstName, this.lastName, this.role);
	}

	/**
	 * Updates the user's password.
	 *
	 * @param newPassword plain text password (must be at least 8 characters)
	 * @throws IllegalArgumentException if password is too weak
	 */

	public void changePassword(String newPassword) {
		if (newPassword == null || newPassword.length() < 8) {
			throw new IllegalArgumentException("Password too weak");
		}
		this.hashedPassword = PasswordUtils.hashPassword(newPassword);
	}

	/**
	 * Updates the user's role.
	 *
	 * @param newRole new role (must not be {@code null})
	 * @throws IllegalArgumentException if role is null
	 */

	public void changeRole(Role newRole) {
		if (newRole == null) {
			throw new IllegalArgumentException("Role cannot be null");
		}
		this.role = newRole;
	}

	/**
	 * Updates the user's email address.
	 *
	 * @param newEmail new email (must contain '@')
	 * @throws IllegalArgumentException if email is invalid
	 */

	public void changeEmail(String newEmail) {
		if (newEmail == null || !newEmail.contains("@")) {
			throw new IllegalArgumentException("Invalid email");
		}
		this.email = newEmail;
	}

	/** @return the user's first name */
	public String getFirstName() {
		return firstName;
	}

	/** @param firstName the user's first name */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/** @return the user's last name */
	public String getLastName() {
		return lastName;
	}

	/** @param lastName the user's last name */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/** @return the user's email address */
	public String getEmail() {
		return email;
	}

	/** @param email the user's email address */
	public void setEmail(String email) {
		this.email = email;
	}

	/** @return the user's username */
	public String getUsername() {
		return username;
	}

	/** @param username the user's username */
	public void setUsername(String username) {
		this.username = username;
	}

	/** @return the user's unique ID */
	public UUID getUserID() {
		return userID;
	}

	/** @return an unmodifiable list of user's loans */
	public List<Loan> getLoans() {
		return Collections.unmodifiableList(loans);
	}

	/** @return the user's registration date */
	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	/** @return the user's role */
	public Role getRole() {
		return role;
	}
}