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
import lms.application.LoanServiceContext;
import lms.application.LoanStatsService;
import lms.application.NotificationService;
import lms.application.RepositoryContext;
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
 * Main entry point for the Library Management System application.
 * 
 * <p>
 * Bootstraps the application by initializing repositories, services, email notifications,
 * background tasks, test users, and launching the CLI interface. A scheduled task checks
 * for overdue loans every 24 hours.
 * </p>
 * 
 * <p>
 * Default test users: "ahmad" (ADMIN) and "majd" (MEMBER).
 * </p>
 * 
 * @author Majd Awwad
 * @version 3.0
 * @see LibraryCLI
 * @see LoanOverdueChecker
 */
public class LibraryApp {

	/** Scheduled executor for background tasks */
	private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	/**
	 * Main method that initializes and starts the Library Management System.
	 * 
	 * @param args command-line arguments (not used)
	 */
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
		JournalService journalService = new JournalService(journalsRepo);
		AccountService accountService = new AccountService(userRepo);
		String pass1 = PasswordUtils.hashPassword("12345678");
		String pass2 = PasswordUtils.hashPassword("1");

		userRepo.add(new User("Ahmad", "Salameh", "ahmad@example.com", "ahmad", pass1, Role.ADMIN));
		userRepo.add(new User("Majd", "Awwad", "majd@example.com", "majd", pass2, Role.MEMBER));
		 
		EmailService emailService = EmailService.getInstance();

		NotificationService notificationService = new NotificationService(emailService);

		RepositoryContext repositoryContext = new RepositoryContext(userRepo, bookRepo, cdRepo, journalsRepo, loanRepo);
		LoanServiceContext loanServiceContext = new LoanServiceContext(repositoryContext, notificationService);
		LoanService loanService = new LoanService(loanServiceContext);

		LoanStatsService loanStatsService = new LoanStatsService(loanRepo);
		LoanQueryService loanQueryService = new LoanQueryService(loanRepo);

		 
		LibraryCLI cli = new LibraryCLI(AuthService.getInstance(), userService, bookService, loanService, cdService, journalService, notificationService, accountService, loanStatsService, loanQueryService);

		cli.start();
	}
}