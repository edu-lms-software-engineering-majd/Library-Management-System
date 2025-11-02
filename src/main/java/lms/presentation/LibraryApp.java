package lms.presentation;

import lms.application.AccountService;
import lms.application.AuthService;
import lms.application.BookService;
import lms.application.LoanService;
import lms.application.UserService;
import lms.domain.BookRepository;
import lms.domain.LoanRepository;
import lms.domain.UserRepository;
import lms.persistence.StaticBookRepository;
import lms.persistence.StaticLoanRepository;
import lms.persistence.StaticUserRepository;

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
 * <li>Instantiate repositories ({@link StaticUserRepository},
 * {@link StaticBookRepository})</li>
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
 * @author Majd
 * @version 2.0
 */
public class LibraryApp {

	public static void main(String[] args) {

		UserRepository userRepo = new StaticUserRepository();
		BookRepository bookRepo = new StaticBookRepository();
		LoanRepository loanRepo = new StaticLoanRepository();
		
		AuthService authService = new AuthService(userRepo);
		UserService userService = new UserService(userRepo);
		BookService bookService = new BookService(bookRepo, userRepo);
		AccountService accountService = new AccountService(userRepo);

		LibraryCLI cli = new LibraryCLI(authService, userService, bookService, loanService);
		cli.start();
	}
}
