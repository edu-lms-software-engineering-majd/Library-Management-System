package lms.presentation;

import lms.application.AuthService;
import lms.application.BookService;
import lms.application.CDService;
import lms.application.AccountService;
import lms.application.JournalService;
import lms.application.LoanQueryService;
import lms.application.LoanService;
import lms.application.LoanStatsService;
import lms.application.NotificationService;
import lms.application.UserService;

/**
 * Factory class for creating the appropriate CLI implementation based on user role.
 * 
 * <p>
 * Returns {@link AdminCLI} for administrators and {@link UserCLI} for members
 * and librarians based on the current authenticated user's role.
 * </p>
 * 
 * @author Majd
 * @version 2.0
 */
public class CLIFactory {

	/**
	 * Returns the appropriate CLI implementation based on the current user's role.
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
	 * @return a CLI instance appropriate for the current user's role
	 * @throws IllegalStateException if the user's role is unsupported
	 */
	public static CLI getCLI(AuthService authService, UserService userService, BookService bookService,LoanService loanService,CDService cdService, JournalService journalService, NotificationService notificationService, AccountService accountService, LoanStatsService loanStatsService, LoanQueryService loanQueryService) {
		switch (AuthService.getInstance().getCurrentUser().role()) {
		case ADMIN:
			return new AdminCLI(userService, bookService, authService,loanService, cdService, journalService, loanStatsService, loanQueryService);
		case MEMBER:
		case LIBRARIAN:
			return new UserCLI(userService, bookService, cdService, journalService, loanService, notificationService, authService, accountService);
			
		default:
			throw new IllegalStateException("Unsupported role: " + AuthService.getInstance().getCurrentUser().role());
		}
	}

}
