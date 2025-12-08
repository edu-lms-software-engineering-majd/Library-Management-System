package lms.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Journal entities.
 *
 * <p>Defines CRUD operations for journals in the library.</p>
 *
 * @author Majd Awwad
 * @version 1.0
 */
public interface JournalsRepository {

	/**
	 * Adds a new journal.
	 *
	 * @param journal the journal to add
	 * @return true if added, false otherwise
	 */
	boolean addJournal(Journal journal);

	/**
	 * Updates an existing journal.
	 *
	 * @param journal the updated journal
	 * @return true if updated, false otherwise
	 */
	boolean updateJournal(Journal journal);

	/**
	 * Deletes a journal by ID.
	 *
	 * @param id the journal ID
	 * @return true if deleted, false otherwise
	 */
	boolean deleteJournal(UUID id);

	/**
	 * Retrieves a journal by ID.
	 *
	 * @param id the journal ID
	 * @return Optional containing the journal if found
	 */
	Optional<Journal> getJournalById(UUID id);

	/**
	 * Retrieves all journals in the system.
	 *
	 * @return list of all journals
	 */
	List<Journal> getAllJournals();

	/**
	 * Searches journals by title or author.
	 *
	 * @param keyword keyword to match against title or author
	 * @return list of matching journals
	 */
	List<Journal> searchJournals(String keyword);
}
