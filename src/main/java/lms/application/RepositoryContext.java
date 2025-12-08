package lms.application;

import lms.domain.BookRepository;
import lms.domain.CDRepository;
import lms.domain.JournalsRepository;
import lms.domain.LoanRepository;
import lms.domain.UserRepository;

/**
 * Container class that holds all repository instances for the Library Management System.
 * 
 * <p>
 * This context class follows the Dependency Injection pattern by grouping related
 * repositories together. It simplifies dependency management by providing a single
 * object to pass around instead of individual repository instances.
 * </p>
 * 
 * <p>
 * Benefits of using this context:
 * </p>
 * <ul>
 * <li>Reduces constructor parameter count in service classes</li>
 * <li>Centralizes repository validation</li>
 * <li>Makes it easier to add new repositories without changing many signatures</li>
 * <li>Improves code maintainability and testability</li>
 * </ul>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class RepositoryContext {

	private final UserRepository userRepo;
	private final BookRepository bookRepo;
	private final CDRepository cdRepo;
	private final JournalsRepository journalRepo;
	private final LoanRepository loanRepo;

	/**
	 * Constructs a new repository context with all required repositories.
	 * 
	 * @param userRepo the user repository
	 * @param bookRepo the book repository
	 * @param cdRepo the CD repository
	 * @param journalRepo the journal repository
	 * @param loanRepo the loan repository
	 * @throws IllegalArgumentException if any repository is null
	 */
	public RepositoryContext(UserRepository userRepo, BookRepository bookRepo, CDRepository cdRepo,
			JournalsRepository journalRepo, LoanRepository loanRepo) {
		if (userRepo == null || bookRepo == null || cdRepo == null || journalRepo == null || loanRepo == null) {
			throw new IllegalArgumentException("All repositories must be non-null");
		}
		this.userRepo = userRepo;
		this.bookRepo = bookRepo;
		this.cdRepo = cdRepo;
		this.journalRepo = journalRepo;
		this.loanRepo = loanRepo;
	}

	/**
	 * Gets the user repository.
	 * 
	 * @return the user repository instance
	 */
	public UserRepository getUserRepo() {
		return userRepo;
	}

	/**
	 * Gets the book repository.
	 * 
	 * @return the book repository instance
	 */
	public BookRepository getBookRepo() {
		return bookRepo;
	}

	/**
	 * Gets the CD repository.
	 * 
	 * @return the CD repository instance
	 */
	public CDRepository getCdRepo() {
		return cdRepo;
	}

	/**
	 * Gets the journal repository.
	 * 
	 * @return the journal repository instance
	 */
	public JournalsRepository getJournalRepo() {
		return journalRepo;
	}

	/**
	 * Gets the loan repository.
	 * 
	 * @return the loan repository instance
	 */
	public LoanRepository getLoanRepo() {
		return loanRepo;
	}
}
