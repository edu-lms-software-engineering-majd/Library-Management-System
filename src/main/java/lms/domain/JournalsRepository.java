package lms.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Journal entities in the Library Management
 * System.
 *
 * <p>
 * This interface defines CRUD operations for journals, allowing implementations
 * to store data in-memory or in a database.
 * </p>
 *
 * @author
 * @version 1.0
 */
public interface JournalsRepository {

	/**
	 * Adds a new journal to the repository.
	 *
	 * @param journal the journal to add
	 * @return true if added successfully, false otherwise
	 */
	boolean addJournal(Journal journal);

	/**
	 * Updates an existing journal.
	 *
	 * @param journal the updated journal
	 * @return true if updated successfully, false otherwise
	 */
	boolean updateJournal(Journal journal);

	/**
	 * Deletes a journal by its unique identifier.
	 *
	 * @param id the journal's UUID
	 * @return true if deleted successfully, false otherwise
	 */
	boolean deleteJournal(UUID id);

	/**
	 * Retrieves a journal by its unique ID.
	 *
	 * @param id the UUID of the journal
	 * @return an Optional containing the found journal, or empty if not found
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
