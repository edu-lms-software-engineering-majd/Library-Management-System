package lms.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.domain.Journal;
import lms.domain.JournalsRepository;

public class StaticJournalsRepository implements JournalsRepository {

	private static final Map<UUID, Journal> journals = new HashMap<>();
	
	private final static StaticJournalsRepository INSTANCE = new StaticJournalsRepository();
	
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
	
	private StaticJournalsRepository() {
	}
	
	public static StaticJournalsRepository getInstance() {
		
		return INSTANCE;
	}

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
