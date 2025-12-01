package lms.presentation;

import java.util.Scanner;

import lms.application.AccountService;
import lms.application.AuthService;
import lms.application.BookService;
import lms.application.CDService;
import lms.application.JournalService;
import lms.application.LoanService;
import lms.application.NotificationService;
import lms.application.UserDTO;
import lms.application.UserService;
import lms.domain.Role;
import lms.domain.exception.InvalidPasswordException;
import lms.domain.exception.UserNotFoundException;

/**
 * Command-Line Interface (CLI) entry menu for the Library Management System.
 *
 * <p>
 * The {@code LibraryCLI} class represents the main user-facing entry point. It
 * provides the first menu displayed after the program starts, allowing users to
 * log in or exit the system.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 * <li>Display the initial system menu (login, exit)</li>
 * <li>Delegate login handling to {@link AuthService}</li>
 * <li>Redirect authenticated users to the correct role-specific menu:
 * <ul>
 * <li>{@link AdminCLI} for administrators</li>
 * <li>{@link UserCLI} for regular users</li>
 * </ul>
 * </li>
 * <li>Report login errors caused by invalid credentials</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * 
 * <pre>{@code
 * UserRepo userRepo = new StaticUserRepo();
 * AuthService authService = new AuthService(userRepo);
 * UserService userService = new UserService(userRepo);
 * BookService bookService = new BookService(new StaticBookRepo());
 *
 * LibraryCLI cli = new LibraryCLI(authService, userService, bookService);
 * cli.start();
 * }</pre>
 *
 * <h2>Exceptions:</h2>
 * <ul>
 * <li>{@link UserNotFoundException} – thrown if the username is unknown</li>
 * <li>{@link InvalidPasswordException} – thrown if the password is
 * incorrect</li>
 * <li>{@link IllegalAccessException} – thrown if the user has no valid
 * role</li>
 * </ul>
 *
 * <p>
 * This class belongs to the <b>presentation layer</b> of the LMS architecture.
 * It depends on services from the application layer but has no knowledge of
 * persistence or domain internals.
 * </p>
 *
 * @author Majd Awwad
 * @version 2.0
 */
public class LibraryCLI implements CLI {

	/** Scanner for reading user input from the console */
	private final Scanner scanner = new Scanner(System.in);

	
	/** Service responsible for handling authentication. */
	private final AuthService authService;
	/** Service for managing users (delegated to sub-menus). */
	private final UserService userService;
	/** Service for managing books (delegated to sub-menus). */
	private final BookService bookService;
	/** Service for managing loans and borrowing operations (delegated to sub-menus). */
	private final LoanService loanService;
	private final CDService cdService;
	private final JournalService journalService;
	private final NotificationService notificationService;
	private final AccountService accountService;

	/**
	 * Constructs a {@code LibraryCLI} with required services.
	 *
	 * @param authService the authentication service used for login/logout
	 * @param userService the service for user-related operations
	 * @param bookService the service for book-related operations
	 * @param loanService 
	 */
	public LibraryCLI(AuthService authService, UserService userService, BookService bookService, LoanService loanService, CDService cdService, JournalService journalService, NotificationService notificationService, AccountService accountService) {
		this.authService = authService;
		this.bookService = bookService;
		this.userService = userService;
		this.loanService = loanService;
		this.cdService = cdService;
		this.journalService = journalService;
		this.notificationService = notificationService;
		this.accountService = accountService;
	}

	/**
	 * Starts the main system menu loop.
	 *
	 * <p>
	 * Options available:
	 * </p>
	 * <ul>
	 * <li>1 - Login</li>
	 * <li>2 - Exit the system</li>
	 * </ul>
	 *
	 * The loop continues until the user chooses to exit.
	 */
	public void start() {
		while (true) {
			System.out.println("\n=== Library Management System ===");
			System.out.println("1. Login");
			System.out.println("2. Exit the system");
			System.out.print("Choose the option: ");

			int choice = scanner.nextInt();
			scanner.nextLine();

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
	 * Handles the login process by prompting the user for credentials and
	 * delegating authentication to {@link AuthService}.
	 *
	 * <p>
	 * If authentication succeeds, the user is redirected to the appropriate CLI
	 * menu based on their {@link Role}.
	 * </p>
	 */
	private void handleLogin() {
		System.out.print("Enter username: ");
		String username = scanner.next();

		scanner.nextLine();

		System.out.print("Enter password:");
		String password = scanner.nextLine();

		try {
			if (authService.login(username, password)) {
				UserDTO current = AuthService.getCurrentUser();
				System.out.println("Login successful! Welcome, " + current.username());
				CLIFactory.getCLI(this.authService, this.userService, this.bookService, this.loanService, this.cdService, this.journalService, this.notificationService, this.accountService).start();
			}
		} catch (UserNotFoundException | InvalidPasswordException | IllegalAccessException e) {
			System.out.println("Login failed: " + e.getMessage());
		}
	}
}
