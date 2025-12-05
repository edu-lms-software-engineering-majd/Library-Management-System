package lms.application;

import java.util.List;
import java.util.UUID;

import lms.domain.Journal;
import lms.domain.JournalsRepository;
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
 * @version 1.1
 */
public class JournalService {

	private final JournalsRepository journalRepo;
	private final UserRepository userRepo;

	@SuppressWarnings("unused")
	private JournalService() {
		journalRepo = null;
		userRepo = null;
	}

	public JournalService(JournalsRepository journalRepo, UserRepository userRepo) {
		this.journalRepo = journalRepo;
		this.userRepo = userRepo;
	}

	 

	public Journal addJournal(UserDTO userDTO, String title, String author) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		Journal journal = new Journal(title, author);

		boolean added = journalRepo.addJournal(journal);
		if (!added)
			throw new IllegalStateException("Failed to add journal: " + title);

		return journal;
	}

	public Journal addJournal(UserDTO userDTO, String title, String author, int totalCopies)
			throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		Journal journal = new Journal(title, author, totalCopies);

		boolean added = journalRepo.addJournal(journal);
		if (!added)
			throw new IllegalStateException("Failed to add journal: " + title);

		return journal;
	}

	 
	public List<Journal> getAllJournals() {
		return journalRepo.getAllJournals();
	}

	public Journal getJournalById(UUID journalId) {
		return journalRepo.getJournalById(journalId)
				.orElseThrow(() -> new IllegalArgumentException("Journal not found with ID: " + journalId));
	}

	public Journal getJournalBySubId(String subId) {
		List<Journal> matches = getAllJournals().stream().filter(j -> j.getId().toString().startsWith(subId)).toList();

		if (matches.isEmpty())
			throw new IllegalArgumentException("No journal found starting with: " + subId);

		if (matches.size() > 1)
			throw new IllegalArgumentException("Multiple journals match prefix: " + subId);

		return matches.get(0);
	}

	 

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
 
	public boolean deleteJournal(UserDTO userDTO, UUID journalId) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		getJournalById(journalId); // throw if not exists

		return journalRepo.deleteJournal(journalId);
	}

	 

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
