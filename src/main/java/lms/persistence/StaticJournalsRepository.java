package lms.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.domain.Journal;
import lms.domain.JournalRepository;

public class StaticJournalsRepository implements JournalRepository {

	private final Map<UUID, Journal> journals = new HashMap<>();

	@Override
	public boolean addJournal(Journal journal) {
		if (journal == null || journals.containsKey(journal.getId()))
			return false;
		journals.put(journal.getId(), journal);
		return true;
	}

	@Override
	public boolean updateJournal(Journal journal) {
		if (journal == null || !journals.containsKey(journal.getId()))
			return false;
		journals.put(journal.getId(), journal);
		return true;
	}

	@Override
	public boolean deleteJournal(UUID id) {
		return journals.remove(id) != null;
	}

	@Override
	public Optional<Journal> getJournalById(UUID id) {
		return Optional.ofNullable(journals.get(id));
	}

	@Override
	public List<Journal> getAllJournals() {
		return new ArrayList<>(journals.values());
	}

	@Override
	public List<Journal> searchJournals(String keyword) {
		return journals.values().stream()
				.filter(journal -> journal.getTitle().toLowerCase().contains(keyword.toLowerCase())
						|| journal.getAuthor().toLowerCase().contains(keyword.toLowerCase()))
				.collect(Collectors.toList());
	}
}
