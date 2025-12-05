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
	
	static {
		CD cd1 = new CD("Thriller", "Michael Jackson", 3);
		CD cd2 = new CD("The Dark Side of the Moon", "Pink Floyd", 2);
		CD cd3 = new CD("Back in Black", "AC/DC", 2);
		CD cd4 = new CD("Abbey Road", "The Beatles", 1);
		CD cd5 = new CD("Rumours", "Fleetwood Mac", 1);
		
		cds.put(cd1.getId(), cd1);
		cds.put(cd2.getId(), cd2);
		cds.put(cd3.getId(), cd3);
		cds.put(cd4.getId(), cd4);
		cds.put(cd5.getId(), cd5);
	}

	 
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
