package lms.presentation;

import lms.application.AccountService;
import lms.application.AuthService;
import lms.application.BookService;
import lms.application.LoanService;
import lms.application.UserService;
<<<<<<< HEAD
import lms.domain.BookRepository;
import lms.domain.LoanRepository;
import lms.domain.UserRepository;
import lms.persistence.StaticBookRepository;
import lms.persistence.StaticLoanRepository;
import lms.persistence.StaticUserRepository;
||||||| 7160386
import lms.domain.BookRepo;
import lms.domain.UserRepo;
import lms.persistence.StaticBookRepo;
import lms.persistence.StaticUserRepo;
=======
import lms.domain.BookRepository;
import lms.domain.CDRepository;
import lms.domain.JournalRepository;
import lms.domain.LoanRepository;
import lms.domain.UserRepository;
import lms.persistence.StaticBookRepository;
import lms.persistence.StaticCDRepository;
import lms.persistence.StaticJournalRepository;
import lms.persistence.StaticLoanRepository;
import lms.persistence.StaticUserRepository;
>>>>>>> ahmad-salameh

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
<<<<<<< HEAD
 * <li>Instantiate repositories ({@link StaticUserRepository},
 * {@link StaticBookRepository})</li>
 * <li>Initialize core services ({@link AuthService}, {@link UserService},
 * {@link BookService})</li>
 * <li>Inject dependencies into the CLI layer</li>
 * <li>Launch the CLI-based user interface</li>
||||||| 7160386
 *   <li>Instantiate repositories ({@link StaticUserRepo}, {@link StaticBookRepo})</li>
 *   <li>Initialize core services ({@link AuthService}, {@link UserService}, {@link BookService})</li>
 *   <li>Inject dependencies into the CLI layer</li>
 *   <li>Launch the CLI-based user interface</li>
=======
 * <<<<<<< HEAD
 * <li>Instantiate repositories ({@link StaticUserRepo},
 * {@link StaticBookRepo})</li> =======
 * <li>Instantiate repositories ({@link StaticUserRepository},
 * {@link StaticBookRepository})</li> >>>>>>> origin/majd
 * <li>Initialize core services ({@link AuthService}, {@link UserService},
 * {@link BookService})</li>
 * <li>Inject dependencies into the CLI layer</li>
 * <li>Launch the CLI-based user interface</li>
>>>>>>> ahmad-salameh
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

<<<<<<< HEAD
		UserRepository userRepo = new StaticUserRepository();
		BookRepository bookRepo = new StaticBookRepository();
		LoanRepository loanRepo = new StaticLoanRepository();
		
||||||| 7160386
		UserRepo userRepo = new StaticUserRepo();
=======
		UserRepository userRepo = new StaticUserRepository();
		BookRepository bookRepo = new StaticBookRepository();
		LoanRepository loanRepo = new StaticLoanRepository();
		CDRepository cdRepo = new StaticCDRepository();
		JournalRepository journalRepo = new StaticJournalRepository();
		

>>>>>>> ahmad-salameh
		AuthService authService = new AuthService(userRepo);
		UserService userService = new UserService(userRepo);
<<<<<<< HEAD
		BookService bookService = new BookService(bookRepo, userRepo);
		AccountService accountService = new AccountService(userRepo);
		LoanService loanService = new LoanService(bookRepo, accountService);

		LibraryCLI cli = new LibraryCLI(authService, userService, bookService, loanService);
||||||| 7160386

		BookRepo bookRepo = new StaticBookRepo();
		BookService bookService = new BookService(bookRepo);

		LibraryCLI cli = new LibraryCLI(authService, userService, bookService);
=======
		BookService bookService = new BookService(bookRepo, userRepo);
		AccountService accountService = new AccountService(userRepo);
		
	    LoanService loanService = new LoanService(userRepo, bookRepo, cdRepo, journalRepo	, loanRepo, accountService);
		LibraryCLI cli = new LibraryCLI(authService, userService, bookService, loanService);
>>>>>>> ahmad-salameh
		cli.start();
	}
}
