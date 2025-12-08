package lms.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lms.domain.Journal;
import lms.domain.JournalsRepository;

/**
 * In-memory implementation of {@link JournalsRepository} using a static HashMap.
 * 
 * <p>Stores journals in a map with support for CRUD operations and search by title or author.
 * Pre-loaded with sample journal data. This implementation is not thread-safe and intended 
 * for testing purposes.</p>
 * 
 * @author Ahmad Salameh
 * @version 2.0
 */
public class StaticJournalsRepository implements JournalsRepository {

	private static final StaticJournalsRepository INSTANCE = new StaticJournalsRepository();
	private static final Map<UUID, Journal> journals = new HashMap<>();

	private StaticJournalsRepository() {
	}

	/**
	 * Returns the singleton instance of the repository.
	 * 
	 * @return the shared StaticJournalsRepository instance
	 */
	public static StaticJournalsRepository getInstance() {
		return INSTANCE;
	}
	
	static {
		Journal journal1 = new Journal("Nature", "Springer Nature", 5);
		Journal journal2 = new Journal("Science", "American Association for the Advancement of Science", 4);
		Journal journal3 = new Journal("The Lancet", "Elsevier", 3);
		Journal journal4 = new Journal("Cell", "Cell Press", 2);
		Journal journal5 = new Journal("The New England Journal of Medicine", "Massachusetts Medical Society", 2);
		
		journals.put(journal1.getId(), journal1);
		journals.put(journal2.getId(), journal2);
		journals.put(journal3.getId(), journal3);
		journals.put(journal4.getId(), journal4);
		journals.put(journal5.getId(), journal5);
	}

	/**
	 * Validates a journal object before persistence operations.
	 *
	 * @param journal the journal to validate
	 * @throws IllegalArgumentException if validation fails
	 */
	private void validate(Journal journal) {
		if (journal == null)
			throw new IllegalArgumentException("Journal cannot be null");

		if (journal.getTitle() == null || journal.getTitle().isBlank())
			throw new IllegalArgumentException("Journal title cannot be empty");

		if (journal.getAuthor() == null || journal.getAuthor().isBlank())
			throw new IllegalArgumentException("Journal author/publisher cannot be empty");

		if (journal.getTotalCopies() < 0)
			throw new IllegalArgumentException("Total copies cannot be negative");
	}

	/**
	 * Adds a new journal to the repository.
	 *
	 * @param journal the journal to add
	 * @return true if added successfully
	 * @throws IllegalArgumentException if journal is invalid or ID already exists
	 */
	@Override
	public boolean addJournal(Journal journal) {
		validate(journal);

		if (journals.containsKey(journal.getId()))
			throw new IllegalArgumentException("A journal with this ID already exists.");

		journals.put(journal.getId(), journal);
		return true;
	}

	/**
	 * Updates an existing journal in the repository.
	 *
	 * @param journal the journal with updated information
	 * @return true if updated successfully, false if journal not found
	 * @throws IllegalArgumentException if journal is invalid
	 */
	@Override
	public boolean updateJournal(Journal journal) {
		validate(journal);

		if (!journals.containsKey(journal.getId()))
			return false;

		journals.put(journal.getId(), journal);
		return true;
	}

	/**
	 * Deletes a journal from the repository.
	 *
	 * @param id the ID of the journal to delete
	 * @return true if deleted successfully, false if journal not found
	 * @throws IllegalArgumentException if id is null
	 */
	@Override
	public boolean deleteJournal(UUID id) {
		if (id == null)
			throw new IllegalArgumentException("Journal ID cannot be null");

		return journals.remove(id) != null;
	}

	/**
	 * Retrieves a journal by its unique identifier.
	 *
	 * @param id the journal ID
	 * @return an Optional containing the journal if found, otherwise empty
	 */
	@Override
	public Optional<Journal> getJournalById(UUID id) {
		if (id == null)
			return Optional.empty();

		return Optional.ofNullable(journals.get(id));
	}

	/**
	 * Retrieves all journals in the repository.
	 *
	 * @return an immutable list of all journals
	 */
	@Override
	public List<Journal> getAllJournals() {
		return Collections.unmodifiableList(new ArrayList<>(journals.values()));
	}

	/**
	 * Searches for journals by keyword in title or author/publisher name.
	 *
	 * @param keyword the search keyword
	 * @return a list of matching journals, or empty list if keyword is null
	 */
	@Override
	public List<Journal> searchJournals(String keyword) {
		if (keyword == null)
			return List.of();

		String k = keyword.toLowerCase();

		return journals.values().stream()
				.filter(j -> j.getTitle().toLowerCase().contains(k) || j.getAuthor().toLowerCase().contains(k))
				.toList();
	}
}
