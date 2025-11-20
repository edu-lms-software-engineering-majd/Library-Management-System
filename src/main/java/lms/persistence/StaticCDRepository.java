package lms.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lms.domain.CD;
import lms.domain.CDRepository;

public class StaticCDRepository implements CDRepository {

	private static final StaticCDRepository INSTANCE = new StaticCDRepository();

	/** In-memory storage */
	private static final Map<UUID, CD> cds = new HashMap<>();

	private StaticCDRepository() {
	}

	public static StaticCDRepository getInstance() {
		return INSTANCE;
	}

	// ============================================
	// Validation
	// ============================================
	private void validate(CD cd) {
		if (cd == null)
			throw new IllegalArgumentException("CD cannot be null");

		if (cd.getTitle() == null || cd.getTitle().isBlank())
			throw new IllegalArgumentException("CD title cannot be empty");

		if (cd.getArtist() == null || cd.getArtist().isBlank())
			throw new IllegalArgumentException("Artist cannot be empty");

		if (cd.getTotalCopies() < 0)
			throw new IllegalArgumentException("Total copies cannot be negative");
	}

	// ============================================
	// CRUD
	// ============================================

	@Override
	public boolean addCD(CD cd) {
		validate(cd);

		if (cds.containsKey(cd.getId()))
			throw new IllegalArgumentException("A CD with this ID already exists.");

		cds.put(cd.getId(), cd);
		return true;
	}

	@Override
	public boolean updateCD(CD cd) {
		validate(cd);

		if (!cds.containsKey(cd.getId()))
			return false;

		cds.put(cd.getId(), cd);
		return true;
	}

	@Override
	public boolean deleteCD(UUID id) {
		if (id == null)
			throw new IllegalArgumentException("CD ID cannot be null");

		return cds.remove(id) != null;
	}

	@Override
	public Optional<CD> getCDById(UUID id) {
		if (id == null)
			return Optional.empty();

		return Optional.ofNullable(cds.get(id));
	}

	@Override
	public List<CD> getAllCDs() {
		return Collections.unmodifiableList(new ArrayList<>(cds.values()));
	}

	// ============================================
	// Search / Filter (allowed minimal filtering)
	// ============================================

	@Override
	public List<CD> searchCDs(String keyword) {
		if (keyword == null)
			return List.of();

		String k = keyword.toLowerCase();

		return cds.values().stream()
				.filter(cd -> cd.getTitle().toLowerCase().contains(k) || cd.getArtist().toLowerCase().contains(k))
				.toList();
	}
}
