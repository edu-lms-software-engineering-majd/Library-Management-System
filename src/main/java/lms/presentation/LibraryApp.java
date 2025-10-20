package lms.presentation;

import lms.application.AccountService;
import lms.application.AuthService;
import lms.application.BookService;
import lms.application.LoanService;
import lms.application.UserService;
import lms.domain.BookRepo;
import lms.domain.LoanRepo;
import lms.domain.UserRepo;
import lms.persistence.AccountRepo;
import lms.persistence.StaticAccountRepo;
import lms.persistence.StaticBookRepo;
import lms.persistence.StaticLoanRepo;
import lms.persistence.StaticUserRepo;

/**
 * Entry point for the Library Management System (LMS).
 *
 * <p>
 * This class acts as the application bootstrapper. It is responsible for
 * creating the necessary repository and service instances, wiring them
 * together, and starting the main {@link LibraryCLI} interface.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 * <li>Instantiate repositories ({@link StaticUserRepo},
 * {@link StaticBookRepo})</li>
 * <li>Initialize core services ({@link AuthService}, {@link UserService},
 * {@link BookService})</li>
 * <li>Inject dependencies into the CLI layer</li>
 * <li>Launch the CLI-based user interface</li>
 * </ul>
 *
 * <p>
 * This class belongs to the <b>presentation layer</b> but also serves as the
 * composition root for dependency injection. In larger applications, a DI
 * framework (e.g., Spring) would take over this role.
 * </p>
 *
 * <h2>Usage:</h2>
 * 
 * <pre>{@code
 *   java lms.presentation.LibraryApp
 * }</pre>
 *
 * <p>
 * After execution, the main menu is displayed, allowing users to log in and
 * access features based on their roles (admin, librarian, member, etc.).
 * </p>
 *
 * @author Majd Awwad
 * @version 2.0
 */
public class LibraryApp {

	/**
	 * Main method that starts the Library Management System.
	 *
	 * <p>
	 * Initializes repositories and services, injects them into {@link LibraryCLI},
	 * and launches the main CLI loop.
	 * </p>
	 *
	 * @param args command-line arguments (not used)
	 */
	public static void main(String[] args) {

		UserRepo userRepo = new StaticUserRepo();
		AuthService authService = new AuthService(userRepo);
		UserService userService = new UserService(userRepo);

		BookRepo bookRepo = new StaticBookRepo();
		BookService bookService = new BookService(bookRepo);

		AccountRepo accountRepo = new StaticAccountRepo();
		AccountService accountService = new AccountService(accountRepo);

		LoanRepo loanRepo = new StaticLoanRepo();
		LoanService loanService = new LoanService(loanRepo, bookRepo, accountRepo, accountService);

		LibraryCLI cli = new LibraryCLI(authService, userService, bookService, loanService);
		cli.start();
	}
}
