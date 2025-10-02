package lms.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lms.domain.utils.PasswordUtils;

/**
 * Represents a user in the Library Management System.
 * 
 * <p>A {@code User} has personal information (name, email, username),
 * a hashed password, a unique ID, registration date, a role, borrowed items,
 * and a financial account.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class User {

    /** User's first name */
    private String firstName;

    /** User's last name */
    private String lastName;

    /** User's email address */
    private String email;

    /** User's login username */
    private String username;

    /** User's password stored as a hash */
    private String hashedPassword;

    /** Unique identifier for the user */
    private UUID userID;

    /** Date when the user registered */
    private final LocalDate registrationDate;

    /** User's role (e.g., ADMIN, MEMBER) */
    private final Role role;

    /** List of items the user has borrowed */
    private List<Loan> loans;

    /** User's financial account for fines and payments */
    private Account account;

    /**
     * Creates a new user with the given personal info, hashed password, and role.
     * Initializes registration date and user ID automatically.
     * Loans list and account are created empty.
     * 
     * @param firstName      the first name of the user
     * @param lastName       the last name of the user
     * @param email          the email address of the user
     * @param username       the username for login
     * @param hashedPassword hashed password for authentication
     * @param role           the user's role
     */
    public User(String firstName, String lastName, String email, String username, String hashedPassword,
                Role role) {
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
     * Creates a new user with specified loans and account.
     * Calls the main constructor and overrides loans and account.
     * 
     * @param firstName      the first name of the user
     * @param lastName       the last name of the user
     * @param email          the email address of the user
     * @param username       the username for login
     * @param hashedPassword hashed password for authentication
     * @param role           the user's role
     * @param loans          the list of loans for the user
     * @param account        the user's financial account
     */
    public User(String firstName, String lastName, String email, String username, String hashedPassword,
                Role role, List<Loan> loans, Account account) {
        this(firstName, lastName, email, username, hashedPassword, role);
        this.loans = loans;
        this.account = account;
    }

    /**
     * Verifies if a raw password matches the stored hashed password.
     * 
     * @param rawPassword the plain text password to verify
     * @return {@code true} if the password matches, {@code false} otherwise
     */
    public boolean verifyPassword(String rawPassword) {
        String hashedPassword = PasswordUtils.hashPassword(rawPassword);
        return PasswordUtils.verifyPassword(hashedPassword, this.hashedPassword);
    }

    /** @return the user's first name */
    public String getFirstName() { return firstName; }

    /** @param firstName the user's first name */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /** @return the user's last name */
    public String getLastName() { return lastName; }

    /** @param lastName the user's last name */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /** @return the user's email address */
    public String getEmail() { return email; }

    /** @param email the user's email address */
    public void setEmail(String email) { this.email = email; }

    /** @return the user's username */
    public String getUsername() { return username; }

    /** @param username the user's username */
    public void setUsername(String username) { this.username = username; }

    /** @return the user's unique ID */
    public UUID getUserID() { return userID; }

    /** @return an unmodifiable list of user's loans */
    public List<Loan> getLoans() { return Collections.unmodifiableList(loans); }

    /** @return the user's registration date */
    public LocalDate getRegistrationDate() { return registrationDate; }

    /** @return the user's role */
    public Role getRole() { return role; }
}
