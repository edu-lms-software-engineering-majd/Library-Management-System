package lms.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.domain.CD;
import lms.domain.CDRepository;

public class StaticCDRepository implements CDRepository {

	private static final Map<UUID, CD> cds = new HashMap<>();
	
	private static StaticCDRepository instance;
	
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
	
	private StaticCDRepository() {
	}
	
	public static StaticCDRepository getInstance() {
		if (instance == null) {
			instance = new StaticCDRepository();
		}
		return instance;
	}

	@Override
	public boolean addCD(CD cd) {
		if (cd == null || cds.containsKey(cd.getId()))
			return false;
		cds.put(cd.getId(), cd);
		return true;
	}

	@Override
	public boolean updateCD(CD cd) {
		if (cd == null || !cds.containsKey(cd.getId()))
			return false;
		cds.put(cd.getId(), cd);
		return true;
	}

	@Override
	public boolean deleteCD(UUID id) {
		return cds.remove(id) != null;
	}

	@Override
	public Optional<CD> getCDById(UUID id) {
		return Optional.ofNullable(cds.get(id));
	}

	@Override
	public List<CD> getAllCDs() {
		return new ArrayList<>(cds.values());
	}

	@Override
	public List<CD> searchCDs(String keyword) {
		return cds.values().stream().filter(cd -> cd.getTitle().toLowerCase().contains(keyword.toLowerCase())
				|| cd.getArtist().toLowerCase().contains(keyword.toLowerCase())).collect(Collectors.toList());
	}
}
