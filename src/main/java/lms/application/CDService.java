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
 * Original Author: Majd Refactored by: Ahmad Salameh
 * </p>
 *
 * @author Majd Awwad
 * @version 2.0
 */
public class CDService {

	private static final String CD_NOT_FOUND_MSG = "CD not found with ID: ";
	
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

	 

	/**
	 * Creates and persists a new CD (admin-only operation).
	 *
	 * @param userDTO the user attempting to add the CD
	 * @param title the CD title
	 * @param artist the CD artist
	 * @return the created CD entity
	 * @throws PermissionDeniedException if user is not an admin
	 * @throws IllegalStateException if addition fails
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
	 * Creates and persists a new CD with specified total copies (admin-only operation).
	 *
	 * @param userDTO the user attempting to add the CD
	 * @param title the CD title
	 * @param artist the CD artist
	 * @param totalCopies the total number of copies
	 * @return the created CD entity
	 * @throws PermissionDeniedException if user is not an admin
	 * @throws IllegalStateException if addition fails
	 */
	public CD addCD(UserDTO userDTO, String title, String artist, int totalCopies) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		CD cd = new CD(title, artist, totalCopies);

		boolean added = cdRepo.addCD(cd);
		if (!added)
			throw new IllegalStateException("Failed to add CD: " + title + " by " + artist);

		return cd;
	}

	/**
	 * Retrieves all CDs in the system.
	 *
	 * @return a list of all CDs
	 */
	public List<CD> getAllCDs() {
		return cdRepo.getAllCDs();
	}

	/**
	 * Retrieves a CD by its unique identifier.
	 *
	 * @param cdId the CD's unique identifier
	 * @return the CD entity
	 * @throws IllegalArgumentException if CD not found
	 */
	public CD getCDById(UUID cdId) {
		return cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException(CD_NOT_FOUND_MSG + cdId));
	}

	/**
	 * Retrieves a CD using a partial ID match (prefix).
	 *
	 * @param subId the ID prefix to search for
	 * @return the matching CD entity
	 * @throws IllegalArgumentException if no match or multiple matches exist
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

	 

	/**
	 * Updates an existing CD's details (admin-only operation).
	 *
	 * @param userDTO the user attempting to update the CD
	 * @param cdId the ID of the CD to update
	 * @param newTitle the new title (null to keep unchanged)
	 * @param newArtist the new artist (null to keep unchanged)
	 * @param newTotalCopies the new total copies (null to keep unchanged)
	 * @return the updated CD entity
	 * @throws PermissionDeniedException if user is not an admin
	 * @throws IllegalArgumentException if CD not found or invalid update
	 * @throws IllegalStateException if update fails
	 */
	public CD updateCD(UserDTO userDTO, UUID cdId, String newTitle, String newArtist, Integer newTotalCopies)
			throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		CD cd = cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException(CD_NOT_FOUND_MSG + cdId));

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

	/**
	 * Deletes a CD from the system (admin-only operation).
	 *
	 * @param userDTO the user attempting to delete the CD
	 * @param cdId the ID of the CD to delete
	 * @return {@code true} if deletion succeeded
	 * @throws PermissionDeniedException if user is not an admin
	 * @throws IllegalArgumentException if CD not found
	 */
	public boolean deleteCD(UserDTO userDTO, UUID cdId) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException(CD_NOT_FOUND_MSG + cdId));

		return cdRepo.deleteCD(cdId);
	}

	 

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

	 

	public boolean isAvailableCD(UUID cdId) {
		return cdRepo.getCDById(cdId).map(cd -> cd.getAvailableCopies() > 0).orElse(false);
	}

	public boolean isValidCD(UUID cdId) {
		return cdRepo.getCDById(cdId).isPresent();
	}

	 
	/**
	 * Decreases available copies when a CD is borrowed.
	 */
	public void borrowCD(UserDTO userDTO, UUID cdId) {

		CD cd = cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException(CD_NOT_FOUND_MSG + cdId));

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

		CD cd = cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException(CD_NOT_FOUND_MSG + cdId));

		if (cd.getAvailableCopies() >= cd.getTotalCopies())
			throw new IllegalStateException("All copies already returned: " + cd.getTitle());

		cd.incrementAvailableCopies();

		if (!cdRepo.updateCD(cd))
			throw new IllegalStateException("Failed to update CD return status");
	}
}
