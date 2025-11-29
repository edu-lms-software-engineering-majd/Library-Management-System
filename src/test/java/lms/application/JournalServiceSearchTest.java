package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.domain.Journal;
import lms.domain.JournalsRepository;
import lms.domain.UserRepository;

@ExtendWith(MockitoExtension.class)
class JournalServiceSearchTest {

	@Mock
	private JournalsRepository journalRepo;

	@Mock
	private UserRepository userRepo;

	private JournalService journalService;

	@BeforeEach
	void setUp() {
		journalService = new JournalService(journalRepo, userRepo);
	}

	@Test
	void shouldReturnAllWhenKeywordNull() {
		Journal j = org.mockito.Mockito.mock(Journal.class);
		when(journalRepo.getAllJournals()).thenReturn(List.of(j));

		List<Journal> result = journalService.searchJournals((String) null);

		assertEquals(1, result.size());
	}

	@Test
	void shouldReturnAllWhenKeywordBlank() {
		Journal j = org.mockito.Mockito.mock(Journal.class);
		when(journalRepo.getAllJournals()).thenReturn(List.of(j));

		List<Journal> result = journalService.searchJournals("   ");

		assertEquals(1, result.size());
	}

	@Test
	void shouldSearchUsingRepository() {
		when(journalRepo.searchJournals("ai")).thenReturn(List.of());

		journalService.searchJournals("ai");

		verify(journalRepo).searchJournals("ai");
	}

	@Test
	void shouldThrowWhenStrategyNull() {
		assertThrows(IllegalArgumentException.class, () -> journalService.searchJournals(null, "term"));
	}

	@Test
	@SuppressWarnings("unchecked")
	void shouldUseStrategySearch() {
		var strategy = org.mockito.Mockito.mock(lms.application.search.SearchStrategy.class);

		Journal j = org.mockito.Mockito.mock(Journal.class);
		when(journalRepo.getAllJournals()).thenReturn(List.of(j));
		when(strategy.execute(anyList(), anyString())).thenReturn(List.of(j));

		List<Journal> result = journalService.searchJournals(strategy, "test");

		assertEquals(1, result.size());
		verify(strategy).execute(anyList(), eq("test"));
	}
}
