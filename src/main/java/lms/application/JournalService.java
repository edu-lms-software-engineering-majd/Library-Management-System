package lms.application;

import java.util.List;
import java.util.UUID;

import lms.domain.Journal;
import lms.domain.JournalsRepository;
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
 * <li>Interacting with a {@link JournalsRepository} to persist or retrieve
 * journals.</li>
 * </ul>
 *
 * <p>
 * The service does <b>not</b> contain core validation logic. Domain-specific
 * validation rules (e.g., valid title/author/copies) are enforced by the
 * {@link Journal} entity.
 * </p>
 *
 * <p>
 * This design ensures separation of concerns: <i>services coordinate, entities
 * validate, repositories persist</i>.
 * </p>
 *
 * <p>
 * Original Author: Majd Awwad Refactored by: Ahmad Salameh
 * </p>
 *
 * @author Majd Awwad
 * @version 1.1
 */
public class JournalService {

	private final JournalsRepository journalRepo;

	@SuppressWarnings("unused")
	private JournalService() {
		journalRepo = null;
	}

	/**
	 * Constructs a JournalService with the required repository.
	 *
	 * @param journalRepo the repository for accessing journal data
	 */
	public JournalService(JournalsRepository journalRepo) {
		this.journalRepo = journalRepo;
	}

	/**
	 * Creates and persists a new journal (admin-only operation).
	 *
	 * @param userDTO the user attempting to add the journal
	 * @param title the journal title
	 * @param author the journal author
	 * @return the created Journal entity
	 * @throws PermissionDeniedException if user is not an admin
	 * @throws IllegalStateException if addition fails
	 */
	public Journal addJournal(UserDTO userDTO, String title, String author) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		Journal journal = new Journal(title, author);

		boolean added = journalRepo.addJournal(journal);
		if (!added)
			throw new IllegalStateException("Failed to add journal: " + title);

		return journal;
	}

	/**
	 * Creates and persists a new journal with specified total copies (admin-only operation).
	 *
	 * @param userDTO the user attempting to add the journal
	 * @param title the journal title
	 * @param author the journal author
	 * @param totalCopies the total number of copies
	 * @return the created Journal entity
	 * @throws PermissionDeniedException if user is not an admin
	 * @throws IllegalStateException if addition fails
	 */
	public Journal addJournal(UserDTO userDTO, String title, String author, int totalCopies)
			throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		Journal journal = new Journal(title, author, totalCopies);

		boolean added = journalRepo.addJournal(journal);
		if (!added)
			throw new IllegalStateException("Failed to add journal: " + title);

		return journal;
	}

	/**
	 * Retrieves all journals in the system.
	 *
	 * @return a list of all journals
	 */
	public List<Journal> getAllJournals() {
		return journalRepo.getAllJournals();
	}

	/**
	 * Retrieves a journal by its unique identifier.
	 *
	 * @param journalId the journal's unique identifier
	 * @return the Journal entity
	 * @throws IllegalArgumentException if journal not found
	 */
	public Journal getJournalById(UUID journalId) {
		return journalRepo.getJournalById(journalId)
				.orElseThrow(() -> new IllegalArgumentException("Journal not found with ID: " + journalId));
	}

	/**
	 * Retrieves a journal using a partial ID match (prefix).
	 *
	 * @param subId the ID prefix to search for
	 * @return the matching Journal entity
	 * @throws IllegalArgumentException if no match or multiple matches exist
	 */
	public Journal getJournalBySubId(String subId) {
		List<Journal> matches = getAllJournals().stream().filter(j -> j.getId().toString().startsWith(subId)).toList();

		if (matches.isEmpty())
			throw new IllegalArgumentException("No journal found starting with: " + subId);

		if (matches.size() > 1)
			throw new IllegalArgumentException("Multiple journals match prefix: " + subId);

		return matches.get(0);
	}

	 

	/**
	 * Updates an existing journal's details (admin-only operation).
	 *
	 * @param userDTO the user attempting to update the journal
	 * @param journalId the ID of the journal to update
	 * @param newTitle the new title (null to keep unchanged)
	 * @param newAuthor the new author (null to keep unchanged)
	 * @param newTotalCopies the new total copies (null to keep unchanged)
	 * @return the updated Journal entity
	 * @throws PermissionDeniedException if user is not an admin
	 * @throws IllegalArgumentException if journal not found or invalid update
	 * @throws IllegalStateException if update fails
	 */
	public Journal updateJournal(UserDTO userDTO, UUID journalId, String newTitle, String newAuthor,
			Integer newTotalCopies) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		Journal journal = getJournalById(journalId);

		if (newTitle != null)
			journal.setTitle(newTitle);

		if (newAuthor != null)
			journal.setAuthor(newAuthor);

		if (newTotalCopies != null) {
			if (newTotalCopies < journal.getAvailableCopies())
				throw new IllegalArgumentException("Total copies cannot be less than available copies");

			journal.setTotalCopies(newTotalCopies);
		}

		boolean updated = journalRepo.updateJournal(journal);
		if (!updated)
			throw new IllegalStateException("Failed to update journal " + journalId);

		return journal;
	}
 
	/**
	 * Deletes a journal from the system (admin-only operation).
	 *
	 * @param userDTO the user attempting to delete the journal
	 * @param journalId the ID of the journal to delete
	 * @return {@code true} if deletion succeeded
	 * @throws PermissionDeniedException if user is not an admin
	 * @throws IllegalArgumentException if journal not found
	 */
	public boolean deleteJournal(UserDTO userDTO, UUID journalId) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		getJournalById(journalId); // throw if not exists

		return journalRepo.deleteJournal(journalId);
	}

	/**
	 * Searches for journals by keyword.
	 *
	 * @param keyword the search keyword (returns all journals if null or blank)
	 * @return a list of matching journals
	 */
	public List<Journal> searchJournals(String keyword) {
		if (keyword == null || keyword.isBlank())
			return getAllJournals();
		return journalRepo.searchJournals(keyword);
	}

	public List<Journal> searchJournals(lms.application.search.SearchStrategy<Journal> strategy, String searchTerm) {
		if (strategy == null)
			throw new IllegalArgumentException("Search strategy cannot be null");

		return strategy.execute(journalRepo.getAllJournals(), searchTerm);
	}

	 
	public boolean isAvailableJournal(UUID journalId) {
		return journalRepo.getJournalById(journalId).map(j -> !j.isBorrowed()).orElse(false);
	}

	public boolean isValidJournal(UUID journalId) {
		return journalRepo.getJournalById(journalId).isPresent();
	}

	public void borrowJournal(UserDTO userDTO, UUID journalId) {

		Journal journal = getJournalById(journalId);

		if (!journal.isAvailable())
			throw new IllegalStateException("No copies available");

		journal.decrementAvailableCopies();

		if (!journalRepo.updateJournal(journal))
			throw new IllegalStateException("Failed to update borrow status");
	}

	public void returnJournal(UserDTO userDTO, UUID journalId) {

		Journal journal = getJournalById(journalId);

		if (journal.getAvailableCopies() >= journal.getTotalCopies())
			throw new IllegalStateException("All copies already returned");

		journal.incrementAvailableCopies();

		if (!journalRepo.updateJournal(journal))
			throw new IllegalStateException("Failed to update return status");
	}
}
