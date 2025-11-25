package lms.presentation;

import lms.application.*;
import lms.application.notifications.email.EmailService;
import lms.domain.*;
import lms.persistence.*;

/**
 * Entry point for the Library Management System (LMS).
 *
 * This version injects a REAL EmailService (Gmail SMTP) into the
 * NotificationService, which is then provided to the LoanService for overdue
 * and due notifications.
 *
 * @author Refactored by Ahmad Salameh
 */
public class LibraryApp {

	public static void main(String[] args) {

		// -------------------------------
		// Repositories (Singleton Static)
		// -------------------------------
		UserRepository userRepo = StaticUserRepository.getInstance();
		BookRepository bookRepo = StaticBookRepository.getInstance();
		CDRepository cdRepo = StaticCDRepository.getInstance();
		JournalsRepository journalsRepo = StaticJournalsRepository.getInstance();
		LoanRepository loanRepo = StaticLoanRepository.getInstance();

		// -------------------------------
		// Core Services
		// -------------------------------
		AuthService authService = new AuthService(userRepo);
		UserService userService = new UserService(userRepo);
		BookService bookService = new BookService(bookRepo);
		CDService cdService = new CDService(cdRepo);
		JournalService journalService = new JournalService(journalsRepo, userRepo);
		AccountService accountService = new AccountService(userRepo);

		// -------------------------------
		// REAL Email Service (SMTP Gmail)
		// -------------------------------
		EmailService emailService = new EmailService("hmeedsalameh2004@gmail.com", "YOUR_16_CHAR_APP_PASSWORD");

		// -------------------------------
		// Notification Service (Now REAL)
		// -------------------------------
		NotificationService notificationService = new NotificationService(emailService);

		// -------------------------------
		// Loan Service
		// -------------------------------
		LoanService loanService = new LoanService(userRepo, bookRepo, cdRepo, journalsRepo, loanRepo,
				notificationService);

		// -------------------------------
		// CLI Layer
		// -------------------------------
		LibraryCLI cli = new LibraryCLI(authService, userService, bookService, loanService, cdService, journalService);

		cli.start();
	}
}
