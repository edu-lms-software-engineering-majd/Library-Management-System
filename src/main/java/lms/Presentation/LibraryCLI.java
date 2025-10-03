package lms.Presentation;

import lms.application.AuthService;
import lms.domain.Role;
import lms.domain.User;
import lms.domain.exception.InvalidPasswordException;
import lms.domain.exception.UserNotFoundException;

import java.util.Scanner;

/**
 * Command Line Interface (CLI) for the Library Management System.
 *
 * <p>The {@code LibraryCLI} class represents the main entry point
 * for the user to interact with the system. It provides options
 * to log in or exit the system.</p>
 *
 * <p>When the user logs in successfully, the system redirects them
 * to either the {@link AdminCLI} or the {@link UserCLI} depending
 * on their assigned {@link Role}.</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>
 * AuthService authService = new AuthService(repo);
 * LibraryCLI cli = new LibraryCLI(authService);
 * cli.start();
 * </pre>
 *
 * @author أحمد
 * @version 1.0
 */
public class LibraryCLI {

    /** Scanner for reading user input from the console */
    private final Scanner scanner = new Scanner(System.in);

    /** Service responsible for handling authentication */
    private final AuthService authService;

    /**
     * Constructs a {@code LibraryCLI} with the given authentication service.
     *
     * @param authService the authentication service used to manage login/logout
     */
    public LibraryCLI(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Starts the main menu loop of the system.
     * <p>Options available:</p>
     * <ul>
     *   <li>1 - Login</li>
     *   <li>2 - Exit the system</li>
     * </ul>
     * The loop continues until the user chooses to exit.
     */
    public void start() {
        while (true) {
            System.out.println("\n=== Library Management System ===");
            System.out.println("1. Login");
            System.out.println("2. Exit the system");
            System.out.print("Choose the option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    System.out.println("Thank you for using the system!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    /**
     * Handles the login process by asking the user to enter
     * their username and password. If authentication is successful,
     * the user is redirected to the correct menu based on their role.
     *
     * <p>Possible exceptions:</p>
     * <ul>
     *   <li>{@link UserNotFoundException} - if the username does not exist</li>
     *   <li>{@link InvalidPasswordException} - if the password is incorrect</li>
     * </ul>
     */
    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            if (authService.login(username, password)) {
                User current = authService.getCurrentUser();
                System.out.println("Login successful! Welcome, " + current.getUsername());

                // open the right menu based on role
                if (current.getRole() == Role.ADMIN) {
                    new AdminCLI().start();
                } else {
                    new UserCLI().start();
                }
            }
        } catch (UserNotFoundException | InvalidPasswordException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }
}
