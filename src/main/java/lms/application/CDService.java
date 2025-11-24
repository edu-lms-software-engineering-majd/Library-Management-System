package lms.application;

import java.util.List;
import java.util.UUID;

import lms.domain.CD;
import lms.domain.CDRepository;
import lms.domain.exception.PermissionDeniedException;

/**
 * Application-level service responsible for managing CD-related use cases.
 *
 * <p>
 * This service acts as the orchestrator between the presentation layer and the
 * domain/persistence layers. It coordinates:
 * </p>
 *
 * <ul>
 * <li>Authorization and admin checks</li>
 * <li>Delegating validation rules to the {@link CD} entity</li>
 * <li>Persisting and retrieving CDs from the {@link CDRepository}</li>
 * </ul>
 *
 * <p>
 * Core principle: <i>Services coordinate, domain entities validate,
 * repositories persist.</i>
 * </p>
 *
 * <p>
 * Original Author: Majd Refactored by: Ahmad Salameh (2025)
 * </p>
 *
 * @version 2.0
 */
public class CDService {

	private final CDRepository cdRepo;

	/**
	 * Creates a {@code CDService} instance with the required repository.
	 *
	 * @param cdRepo the repository used for CD persistence operations
	 * @throws IllegalArgumentException if the repository is null
	 */
	public CDService(CDRepository cdRepo) {
		if (cdRepo == null)
			throw new IllegalArgumentException("CDRepository cannot be null");
		this.cdRepo = cdRepo;
	}

	// =====================================================
	// Creation
	// =====================================================

	/**
	 * Creates and stores a new CD (admin-only).
	 */
	public CD addCD(UserDTO userDTO, String title, String artist) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		CD cd = new CD(title, artist);

		boolean added = cdRepo.addCD(cd);
		if (!added)
			throw new IllegalStateException("Failed to add CD: " + title + " by " + artist);

		return cd;
	}

	/**
	 * Creates and stores a new CD with total copies (admin-only).
	 */
	public CD addCD(UserDTO userDTO, String title, String artist, int totalCopies) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		CD cd = new CD(title, artist, totalCopies);

		boolean added = cdRepo.addCD(cd);
		if (!added)
			throw new IllegalStateException("Failed to add CD: " + title + " by " + artist);

		return cd;
	}

	// =====================================================
	// Retrieval
	// =====================================================

	/** Returns all CDs in the system. */
	public List<CD> getAllCDs() {
		return cdRepo.getAllCDs();
	}

	/** Retrieves a CD by ID or throws an error. */
	public CD getCDById(UUID cdId) {
		return cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException("CD not found with ID: " + cdId));
	}

	/**
	 * Retrieves a CD by ID prefix (substring match).
	 */
	public CD getCDBySubId(String subId) {
		List<CD> matches = cdRepo.getAllCDs().stream().filter(cd -> cd.getId().toString().startsWith(subId)).toList();

		if (matches.isEmpty())
			throw new IllegalArgumentException("No CD found with ID starting with: " + subId);

		if (matches.size() > 1)
			throw new IllegalArgumentException(
					"Multiple CDs found with ID starting with: " + subId + ". Provide more characters.");

		return matches.get(0);
	}

	// =====================================================
	// Update
	// =====================================================

	/**
	 * Updates a CD's details (admin-only).
	 */
	public CD updateCD(UserDTO userDTO, UUID cdId, String newTitle, String newArtist, Integer newTotalCopies)
			throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		CD cd = cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException("CD not found with ID: " + cdId));

		if (newTitle != null)
			cd.setTitle(newTitle);
		if (newArtist != null)
			cd.setArtist(newArtist);

		if (newTotalCopies != null) {
			if (newTotalCopies < cd.getAvailableCopies())
				throw new IllegalArgumentException("New total copies (" + newTotalCopies
						+ ") cannot be less than available copies (" + cd.getAvailableCopies() + ")");
			cd.setTotalCopies(newTotalCopies);
		}

		if (!cdRepo.updateCD(cd))
			throw new IllegalStateException("Failed to update CD with ID: " + cdId);

		return cd;
	}

	// =====================================================
	// Delete
	// =====================================================

	/**
	 * Deletes a CD from the system (admin-only).
	 */
	public boolean deleteCD(UserDTO userDTO, UUID cdId) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException("CD not found with ID: " + cdId));

		return cdRepo.deleteCD(cdId);
	}

	// =====================================================
	// Searching
	// =====================================================

	public List<CD> searchCDs(String keyword) {
		if (keyword == null || keyword.isBlank())
			return getAllCDs();
		return cdRepo.searchCDs(keyword);
	}

	public List<CD> searchCDs(lms.application.search.SearchStrategy<CD> strategy, String searchTerm) {
		if (strategy == null)
			throw new IllegalArgumentException("Search strategy cannot be null");

		return strategy.execute(cdRepo.getAllCDs(), searchTerm);
	}

	// =====================================================
	// Availability
	// =====================================================

	public boolean isAvailableCD(UUID cdId) {
		return cdRepo.getCDById(cdId).map(cd -> cd.getAvailableCopies() > 0).orElse(false);
	}

	public boolean isValidCD(UUID cdId) {
		return cdRepo.getCDById(cdId).isPresent();
	}

	// =====================================================
	// Borrowing
	// =====================================================

	/**
	 * Decreases available copies when a CD is borrowed.
	 */
	public void borrowCD(UserDTO userDTO, UUID cdId) {

		CD cd = cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException("CD not found with ID: " + cdId));

		if (!cd.isAvailable())
			throw new IllegalStateException("No available copies to borrow: " + cd.getTitle());

		cd.decrementAvailableCopies();

		if (!cdRepo.updateCD(cd))
			throw new IllegalStateException("Failed to update CD borrow status");
	}

	/**
	 * Increases available copies when a CD is returned.
	 */
	public void returnCD(UserDTO userDTO, UUID cdId) {

		CD cd = cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException("CD not found with ID: " + cdId));

		if (cd.getAvailableCopies() >= cd.getTotalCopies())
			throw new IllegalStateException("All copies already returned: " + cd.getTitle());

		cd.incrementAvailableCopies();

		if (!cdRepo.updateCD(cd))
			throw new IllegalStateException("Failed to update CD return status");
	}
}
