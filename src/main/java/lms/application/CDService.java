package lms.application;

import java.util.List;
import java.util.UUID;

import lms.domain.CD;
import lms.domain.CDRepository;
import lms.domain.UserRepository;
import lms.domain.exception.PermissionDeniedException;

/**
 * Application service for coordinating CD management use cases.
 *
 * <p>
 * This service acts as an orchestrator between the user-facing layer and the
 * domain/persistence layers. It is responsible for:
 * </p>
 * <ul>
 * <li>Enforcing authorization rules (e.g., only admins can add CDs).</li>
 * <li>Delegating CD creation to the {@link CD} domain entity, which
 * encapsulates its own validation rules.</li>
 * <li>Interacting with a {@link CDRepository} to persist or retrieve CDs.</li>
 * </ul>
 *
 * <p>
 * This design ensures a clear separation of concerns: <i>services coordinate,
 * entities validate themselves, repositories persist</i>.
 * </p>
 *
 * @author Majd
 * @version 1.0
 */
public class CDService {

	private final CDRepository cdRepo;
	private final UserRepository userRepo;

	private CDService() {
		cdRepo = null;
		userRepo = null;
	}

	public CDService(CDRepository cdRepo, UserRepository userRepo) {
		this.cdRepo = cdRepo;
		this.userRepo = userRepo;
	}

	public CD addCD(UserDTO userDTO, String title, String artist) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		CD cd = new CD(title, artist);

		boolean added = cdRepo.addCD(cd);
		if (!added) {
			throw new IllegalStateException("Failed to add CD: " + title + " by " + artist);
		}

		return cd;
	}

	public CD addCD(UserDTO userDTO, String title, String artist, int totalCopies) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		CD cd = new CD(title, artist, totalCopies);

		boolean added = cdRepo.addCD(cd);
		if (!added) {
			throw new IllegalStateException("Failed to add CD: " + title + " by " + artist);
		}

		return cd;
	}

	public List<CD> getAllCDs() {
		return cdRepo.getAllCDs();
	}

	public CD getCDById(UUID cdId) {
		return cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException("CD not found with ID: " + cdId));
	}

	/**
	 * Retrieves a CD by a partial ID match (substring).
	 */
	public CD getCDBySubId(String subId) {
		List<CD> allCDs = getAllCDs();
		List<CD> matches = allCDs.stream().filter(cd -> cd.getId().toString().startsWith(subId)).toList();

		if (matches.isEmpty()) {
			throw new IllegalArgumentException("No CD found with ID starting with: " + subId);
		}

		if (matches.size() > 1) {
			throw new IllegalArgumentException(
					"Multiple CDs found with ID starting with: " + subId + ". Please provide more characters.");
		}

		return matches.get(0);
	}

	public CD updateCD(UserDTO userDTO, UUID cdId, String newTitle, String newArtist, Integer newTotalCopies)
			throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		CD cd = cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException("CD not found with ID: " + cdId));

		if (newTitle != null) {
			cd.setTitle(newTitle);
		}
		if (newArtist != null) {
			cd.setArtist(newArtist);
		}
		if (newTotalCopies != null) {
			if (newTotalCopies < cd.getAvailableCopies()) {
				throw new IllegalArgumentException("New total copies (" + newTotalCopies
						+ ") cannot be less than available copies (" + cd.getAvailableCopies() + ")");
			}
			cd.setTotalCopies(newTotalCopies);
		}

		boolean updated = cdRepo.updateCD(cd);
		if (!updated) {
			throw new IllegalStateException("Failed to update CD with ID: " + cdId);
		}

		return cd;
	}

	public boolean deleteCD(UserDTO userDTO, UUID cdId) throws PermissionDeniedException {
		AuthorizationService.ensureAdmin(userDTO);

		cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException("CD not found with ID: " + cdId));

		return cdRepo.deleteCD(cdId);
	}

	public List<CD> searchCDs(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return getAllCDs();
		}
		return cdRepo.searchCDs(keyword);
	}

	public List<CD> searchCDs(lms.application.search.SearchStrategy<CD> strategy, String searchTerm) {
		if (strategy == null) {
			throw new IllegalArgumentException("Search strategy cannot be null");
		}
		return strategy.execute(cdRepo.getAllCDs(), searchTerm);
	}

	public boolean isAvailableCD(UUID cdId) {
		return cdRepo.getCDById(cdId).map(cd -> !cd.isBorrowed()).orElse(false);
	}

	public boolean isValidCD(UUID cdId) {
		return cdRepo.getCDById(cdId).isPresent();
	}

	public void borrowCD(UserDTO userDTO, UUID cdId) {

		CD cd = cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException("CD not found with ID: " + cdId));

		if (!cd.isAvailable()) {
			throw new IllegalStateException("No copies available to borrow: " + cd.getTitle());
		}

		cd.decrementAvailableCopies();
		boolean updated = cdRepo.updateCD(cd);
		if (!updated) {
			throw new IllegalStateException("Failed to update CD borrow status");
		}
	}

	public void returnCD(UserDTO userDTO, UUID cdId) {

		CD cd = cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException("CD not found with ID: " + cdId));

		if (cd.getAvailableCopies() >= cd.getTotalCopies()) {
			throw new IllegalStateException("All copies already returned: " + cd.getTitle());
		}

		cd.incrementAvailableCopies();
		boolean updated = cdRepo.updateCD(cd);
		if (!updated) {
			throw new IllegalStateException("Failed to update CD return status");
		}
	}
}
