package lms.presentation;

import java.util.Scanner;

import lms.application.AccountService;
import lms.application.AuthService;
import lms.application.BookService;
import lms.application.CDService;
import lms.application.JournalService;
import lms.application.LoanQueryService;
import lms.application.LoanService;
import lms.application.LoanStatsService;
import lms.application.NotificationService;
import lms.application.UserDTO;
import lms.application.UserService;
import lms.domain.exception.InvalidPasswordException;
import lms.domain.exception.UserNotFoundException;

/**
 * Main entry menu for the Library Management System.
 *
 * <p>
 * Provides the initial login interface and redirects authenticated users to
 * the appropriate role-specific menu ({@link AdminCLI} or {@link UserCLI}).
 * Uses {@link CLIFactory} to determine the correct CLI based on user role.
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
	private final LoanStatsService loanStatsService;
	private final LoanQueryService loanQueryService;

	/**
	 * Constructs a LibraryCLI with all required services.
	 *
	 * @param authService the authentication service
	 * @param userService the user management service
	 * @param bookService the book management service
	 * @param loanService the loan management service
	 * @param cdService the CD management service
	 * @param journalService the journal management service
	 * @param notificationService the notification service
	 * @param accountService the account management service
	 * @param loanStatsService the loan statistics service
	 * @param loanQueryService the loan query service
	 */
	public LibraryCLI(AuthService authService, UserService userService, BookService bookService, LoanService loanService, CDService cdService, JournalService journalService, NotificationService notificationService, AccountService accountService, LoanStatsService loanStatsService, LoanQueryService loanQueryService) {
		this.authService = authService;
		this.bookService = bookService;
		this.userService = userService;
		this.loanService = loanService;
		this.cdService = cdService;
		this.journalService = journalService;
		this.notificationService = notificationService;
		this.accountService = accountService;
		this.loanStatsService = loanStatsService;
		this.loanQueryService = loanQueryService;
	}

	/**
	 * Starts the main login menu loop.
	 * Presents options to login or exit, and continues until the user exits.
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
	 * Handles the login process and redirects to the appropriate role-based CLI.
	 * Prompts for username and password, then authenticates via {@link AuthService}.
	 */
	private void handleLogin() {
		System.out.print("Enter username: ");
		String username = scanner.next();

		scanner.nextLine();

		System.out.print("Enter password:");
		String password = scanner.nextLine();

		try {
			if (authService.login(username, password)) {
				UserDTO current = AuthService.getInstance().getCurrentUser();
				System.out.println("Login successful! Welcome, " + current.username());
				CLIFactory.getCLI(this.authService, this.userService, this.bookService, this.loanService, this.cdService, this.journalService, this.notificationService, this.accountService, this.loanStatsService, this.loanQueryService).start();
			}
		} catch (UserNotFoundException | InvalidPasswordException | IllegalAccessException e) {
			System.out.println("Login failed: " + e.getMessage());
		}
	}
}
