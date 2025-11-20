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

public class StaticJournalsRepository implements JournalsRepository {

	private static final StaticJournalsRepository INSTANCE = new StaticJournalsRepository();

	/** In-memory storage */
	private static final Map<UUID, Journal> journals = new HashMap<>();

	private StaticJournalsRepository() {
	}

	public static StaticJournalsRepository getInstance() {
		return INSTANCE;
	}

	// ============================================
	// Validation
	// ============================================
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

	// ============================================
	// CRUD
	// ============================================

	@Override
	public boolean addJournal(Journal journal) {
		validate(journal);

		if (journals.containsKey(journal.getId()))
			throw new IllegalArgumentException("A journal with this ID already exists.");

		journals.put(journal.getId(), journal);
		return true;
	}

	@Override
	public boolean updateJournal(Journal journal) {
		validate(journal);

		if (!journals.containsKey(journal.getId()))
			return false;

		journals.put(journal.getId(), journal);
		return true;
	}

	@Override
	public boolean deleteJournal(UUID id) {
		if (id == null)
			throw new IllegalArgumentException("Journal ID cannot be null");

		return journals.remove(id) != null;
	}

	@Override
	public Optional<Journal> getJournalById(UUID id) {
		if (id == null)
			return Optional.empty();

		return Optional.ofNullable(journals.get(id));
	}

	@Override
	public List<Journal> getAllJournals() {
		return Collections.unmodifiableList(new ArrayList<>(journals.values()));
	}

	// ============================================
	// Filtering (Allowed minimal search)
	// ============================================

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
