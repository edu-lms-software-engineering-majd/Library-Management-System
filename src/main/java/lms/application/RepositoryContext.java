package lms.application;

import lms.domain.BookRepository;
import lms.domain.CDRepository;
import lms.domain.JournalsRepository;
import lms.domain.LoanRepository;
import lms.domain.UserRepository;

public class RepositoryContext {

	private final UserRepository userRepo;
	private final BookRepository bookRepo;
	private final CDRepository cdRepo;
	private final JournalsRepository journalRepo;
	private final LoanRepository loanRepo;

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

	public UserRepository getUserRepo() {
		return userRepo;
	}

	public BookRepository getBookRepo() {
		return bookRepo;
	}

	public CDRepository getCdRepo() {
		return cdRepo;
	}

	public JournalsRepository getJournalRepo() {
		return journalRepo;
	}

	public LoanRepository getLoanRepo() {
		return loanRepo;
	}
}
