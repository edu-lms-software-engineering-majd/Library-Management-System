package lms.presentation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lms.application.AccountService;
import lms.application.AuthService;
import lms.application.BookService;
import lms.application.CDService;
import lms.application.JournalService;
import lms.application.LoanQueryService;
import lms.application.LoanService;
import lms.application.LoanStatsService;
import lms.application.NotificationService;
import lms.application.UserService;
import lms.application.email.EmailService;
import lms.application.task.LoanOverdueChecker;
import lms.domain.BookRepository;
import lms.domain.CDRepository;
import lms.domain.JournalsRepository;
import lms.domain.LoanRepository;
import lms.domain.Role;
import lms.domain.User;
import lms.domain.UserRepository;
import lms.domain.utils.PasswordUtils;
import lms.persistence.StaticBookRepository;
import lms.persistence.StaticCDRepository;
import lms.persistence.StaticJournalsRepository;
import lms.persistence.StaticLoanRepository;
import lms.persistence.StaticUserRepository;

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

	private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public static void main(String[] args) {
	 
		scheduler.scheduleAtFixedRate(new LoanOverdueChecker(), 1, (long )24 * 60, TimeUnit.MINUTES);

		 
		UserRepository userRepo = StaticUserRepository.getInstance();
		
		
		
		BookRepository bookRepo = StaticBookRepository.getInstance();
		CDRepository cdRepo = StaticCDRepository.getInstance();
		JournalsRepository journalsRepo = StaticJournalsRepository.getInstance();
		LoanRepository loanRepo = StaticLoanRepository.getInstance();
 
		UserService userService = new UserService(userRepo);
		BookService bookService = new BookService(bookRepo);
		CDService cdService = new CDService(cdRepo);
		JournalService journalService = new JournalService(journalsRepo, userRepo);
		AccountService accountService = new AccountService(userRepo);
		String pass1 = PasswordUtils.hashPassword("12345678");
		String pass2 = PasswordUtils.hashPassword("1");

		userRepo.add(new User("Ahmad", "Salameh", "ahmad@example.com", "ahmad", pass1, Role.ADMIN));
		userRepo.add(new User("Majd", "Awwad", "majd@example.com", "majd", pass2, Role.MEMBER));
		 
		EmailService emailService = EmailService.getInstance();

	 
		NotificationService notificationService = new NotificationService(emailService);

		 
		LoanService loanService = new LoanService(userRepo, bookRepo, cdRepo, journalsRepo, loanRepo,
				notificationService);

		LoanStatsService loanStatsService = new LoanStatsService(loanRepo);
		LoanQueryService loanQueryService = new LoanQueryService(loanRepo);

		 
		LibraryCLI cli = new LibraryCLI(AuthService.getInstance(), userService, bookService, loanService, cdService, journalService, notificationService, accountService, loanStatsService, loanQueryService);

		cli.start();
	}
}