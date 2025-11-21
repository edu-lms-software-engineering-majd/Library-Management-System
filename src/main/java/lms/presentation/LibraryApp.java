package lms.presentation;

import lms.application.AccountService;
import lms.application.AuthService;
import lms.application.BookService;
import lms.application.CDService;
import lms.application.JournalService;
import lms.application.LoanService;
import lms.application.NotificationService;
import lms.application.UserService;
import lms.domain.BookRepository;
import lms.domain.CDRepository;
import lms.domain.JournalsRepository;
import lms.domain.LoanRepository;
import lms.domain.UserRepository;
import lms.persistence.StaticBookRepository;
import lms.persistence.StaticCDRepository;
import lms.persistence.StaticJournalsRepository;
import lms.persistence.StaticLoanRepository;
import lms.persistence.StaticUserRepository;

/**
 * Entry point for the Library Management System (LMS).
 */
public class LibraryApp {

	public static void main(String[] args) {

		UserRepository userRepo = StaticUserRepository.getInstance();
		BookRepository bookRepo = StaticBookRepository.getInstance();
		CDRepository cdRepo = StaticCDRepository.getInstance();
		JournalsRepository journalsRepo = StaticJournalsRepository.getInstance();
		LoanRepository loanRepo = StaticLoanRepository.getInstance();

		AuthService authService = new AuthService(userRepo);
		UserService userService = new UserService(userRepo);
		BookService bookService = new BookService(bookRepo);
		CDService cdService = new CDService(cdRepo);
		JournalService journalService = new JournalService(journalsRepo, userRepo);
		AccountService accountService = new AccountService(userRepo);
		NotificationService notificationService = new NotificationService();

		LoanService loanService = new LoanService(userRepo, bookRepo, cdRepo, journalsRepo, loanRepo,
				notificationService);

		LibraryCLI cli = new LibraryCLI(authService, userService, bookService, loanService, cdService, journalService);
		cli.start();
	}
}
