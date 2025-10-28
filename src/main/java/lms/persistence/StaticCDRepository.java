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

	private final Map<UUID, CD> cds = new HashMap<>();

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
