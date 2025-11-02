package lms.application;

import java.util.List;
import java.util.UUID;

import lms.domain.Journal;
import lms.domain.JournalRepository;
import lms.domain.UserRepository;
import lms.domain.exception.PermissionDeniedException;

/**
 * Application service for coordinating journal management use cases.
 *
 * <p>
 * This service acts as an orchestrator between the user-facing layer and the
 * domain/persistence layers. It is responsible for:
 * </p>
 * <ul>
 * <li>Enforcing authorization rules (e.g., only admins can add journals).</li>
 * <li>Delegating journal creation to the {@link Journal} domain entity, which
 * encapsulates its own validation rules.</li>
 * <li>Interacting with a {@link JournalRepository} to persist or retrieve journals.</li>
 * </ul>
 *
 * <p>
 * The service does <b>not</b> contain core validation logic for journal fields.
 * Such domain-specific rules (e.g., non-empty title, valid author) are
 * enforced directly inside the {@link Journal} entity itself.
 * </p>
 *
 * <p>
 * This design ensures a clear separation of concerns: <i>services coordinate,
 * entities validate themselves, repositories persist</i>.
 * </p>
 *
 * @author Majd Awwad
 * @version 1.0
 */
public class JournalService {

	private final JournalRepository journalRepo;
	private final UserRepository userRepo;

	@SuppressWarnings("unused")
	private JournalService() {
		journalRepo = null;
		userRepo = null;
	}

	/**
	 * Creates a new {@code JournalService} with the given repositories.
	 *
	 * @param journalRepo the repository used for persisting and retrieving journals
	 * @param userRepo    the repository for user-related operations
	 */
	public JournalService(JournalRepository journalRepo, UserRepository userRepo) {
		this.journalRepo = journalRepo;
		this.userRepo = userRepo;
	}

	/**
	 * Adds a new journal to the system, if the requesting user has admin privileges.
	 *
	 * <p>
	 * This method performs the following steps:
	 * </p>
	 * <ol>
	 * <li>Verifies that the given user is an administrator.</li>
	 * <li>Constructs a {@link Journal}, which performs its own validation.</li>
	 * <li>Attempts to persist the journal using {@link JournalRepository}.</li>
	 * </ol>
	 *
	 * @param userDTO the user attempting the action (must be admin)
	 * @param title   the title of the journal
	 * @param author  the author of the journal
	 * @return the newly created {@link Journal}
	 * @throws PermissionDeniedException if the user is not an admin
	 * @throws IllegalArgumentException  if {@link Journal} validation fails
	 * @throws IllegalStateException     if the journal could not be added to the
	 *                                   repository
	 */
	public Journal addJournal(UserDTO userDTO, String title, String author)
			throws PermissionDeniedException, IllegalStateException, IllegalArgumentException {

		AuthorizationService.ensureAdmin(userDTO);

		Journal journal = new Journal(title, author);

		boolean added = journalRepo.addJournal(journal);
		if (!added) {
			throw new IllegalStateException("Failed to add journal: " + title + " by " + author);
		}

		return journal;
	}

	/**
	 * Retrieves all journals currently in the repository.
	 *
	 * @return a list of all {@link Journal} entities
	 */
	public List<Journal> getAllJournals() {
		return journalRepo.getAllJournals();
	}

	/**
	 * Retrieves a journal by its unique identifier.
	 *
	 * @param journalId the UUID of the journal
	 * @return the {@link Journal} if found
	 * @throws IllegalArgumentException if the journal ID is not found
	 */
	public Journal getJournalById(UUID journalId) {
		return journalRepo.getJournalById(journalId)
				.orElseThrow(() -> new IllegalArgumentException("Journal not found with ID: " + journalId));
	}

	/**
	 * Updates an existing journal's information, if the requesting user has admin
	 * privileges.
	 *
	 * @param userDTO   the user attempting the action (must be admin)
	 * @param journalId the UUID of the journal to update
	 * @param newTitle  the new title (if null, keeps existing)
	 * @param newAuthor the new author (if null, keeps existing)
	 * @return the updated {@link Journal}
	 * @throws PermissionDeniedException if the user is not an admin
	 * @throws IllegalArgumentException  if the journal is not found or validation fails
	 * @throws IllegalStateException     if the update fails
	 */
	public Journal updateJournal(UserDTO userDTO, UUID journalId, String newTitle, String newAuthor)
			throws PermissionDeniedException, IllegalArgumentException, IllegalStateException {

		AuthorizationService.ensureAdmin(userDTO);

		Journal journal = journalRepo.getJournalById(journalId)
				.orElseThrow(() -> new IllegalArgumentException("Journal not found with ID: " + journalId));

		if (newTitle != null && !newTitle.isBlank()) {
			journal.setTitle(newTitle);
		}
		if (newAuthor != null && !newAuthor.isBlank()) {
			journal.setAuthor(newAuthor);
		}

		boolean updated = journalRepo.updateJournal(journal);
		if (!updated) {
			throw new IllegalStateException("Failed to update journal with ID: " + journalId);
		}

		return journal;
	}

	/**
	 * Deletes a journal from the system, if the requesting user has admin privileges.
	 *
	 * @param userDTO   the user attempting the action (must be admin)
	 * @param journalId the UUID of the journal to delete
	 * @throws PermissionDeniedException if the user is not an admin
	 * @throws IllegalArgumentException  if the journal is not found
	 */
	public void deleteJournal(UserDTO userDTO, UUID journalId) throws PermissionDeniedException, IllegalArgumentException {

		AuthorizationService.ensureAdmin(userDTO);

		boolean deleted = journalRepo.deleteJournal(journalId);
		if (!deleted) {
			throw new IllegalArgumentException("Journal not found with ID: " + journalId);
		}
	}

	/**
	 * Searches for journals by keyword (matches title or author).
	 *
	 * @param keyword the search keyword
	 * @return a list of matching {@link Journal} entities
	 */
	public List<Journal> searchJournals(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return getAllJournals();
		}
		return journalRepo.searchJournals(keyword);
	}

	/**
	 * Checks if a journal is available (not borrowed).
	 *
	 * @param journalId the UUID of the journal
	 * @return true if the journal exists and is not borrowed, false otherwise
	 */
	public boolean isAvailableJournal(UUID journalId) {
		return journalRepo.getJournalById(journalId)
				.map(journal -> !journal.isBorrowed())
				.orElse(false);
	}

	/**
	 * Validates if a journal exists in the repository.
	 *
	 * @param journalId the UUID of the journal
	 * @return true if the journal exists, false otherwise
	 */
	public boolean isValidJournal(UUID journalId) {
		return journalRepo.getJournalById(journalId).isPresent();
	}

	/**
	 * Marks a journal as borrowed.
	 *
	 * @param userDTO   the user attempting the action
	 * @param journalId the UUID of the journal to borrow
	 * @throws IllegalArgumentException  if the journal is not found
	 * @throws IllegalStateException     if the journal is already borrowed
	 */
	public void borrowJournal(UserDTO userDTO, UUID journalId) throws IllegalArgumentException, IllegalStateException {

		Journal journal = journalRepo.getJournalById(journalId)
				.orElseThrow(() -> new IllegalArgumentException("Journal not found with ID: " + journalId));

		if (journal.isBorrowed()) {
			throw new IllegalStateException("Journal is already borrowed: " + journal.getTitle());
		}

		journal.setBorrowed(true);
		boolean updated = journalRepo.updateJournal(journal);
		if (!updated) {
			throw new IllegalStateException("Failed to update journal borrow status");
		}
	}

	/**
	 * Marks a journal as returned.
	 *
	 * @param userDTO   the user attempting the action
	 * @param journalId the UUID of the journal to return
	 * @throws IllegalArgumentException if the journal is not found
	 * @throws IllegalStateException    if the journal was not borrowed
	 */
	public void returnJournal(UserDTO userDTO, UUID journalId) throws IllegalArgumentException, IllegalStateException {

		Journal journal = journalRepo.getJournalById(journalId)
				.orElseThrow(() -> new IllegalArgumentException("Journal not found with ID: " + journalId));

		if (!journal.isBorrowed()) {
			throw new IllegalStateException("Journal is not currently borrowed: " + journal.getTitle());
		}

		journal.setBorrowed(false);
		boolean updated = journalRepo.updateJournal(journal);
		if (!updated) {
			throw new IllegalStateException("Failed to update journal return status");
		}
	}
}
