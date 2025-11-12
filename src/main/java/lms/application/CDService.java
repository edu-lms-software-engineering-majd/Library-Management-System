
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
 * The service does <b>not</b> contain core validation logic for CD fields. Such
 * domain-specific rules (e.g., non-empty title, valid artist) are enforced
 * directly inside the {@link CD} entity itself.
 * </p>
 *
 * <p>
 * This design ensures a clear separation of concerns: <i>services coordinate,
 * entities validate themselves, repositories persist</i>.
 * </p>
 *
 * @author Majd Awwad
 * @version 1.0
 */
public class CDService {

	private final CDRepository cdRepo;
	private final UserRepository userRepo;

	@SuppressWarnings("unused")
	private CDService() {
		cdRepo = null;
		userRepo = null;
	}

	/**
	 * Creates a new {@code CDService} with the given repositories.
	 *
	 * @param cdRepo   the repository used for persisting and retrieving CDs
	 * @param userRepo the repository for user-related operations
	 */
	public CDService(CDRepository cdRepo, UserRepository userRepo) {
		this.cdRepo = cdRepo;
		this.userRepo = userRepo;
	}

	/**
	 * Adds a new CD to the system, if the requesting user has admin privileges.
	 *
	 * <p>
	 * This method performs the following steps:
	 * </p>
	 * <ol>
	 * <li>Verifies that the given user is an administrator.</li>
	 * <li>Constructs a {@link CD}, which performs its own validation.</li>
	 * <li>Attempts to persist the CD using {@link CDRepository}.</li>
	 * </ol>
	 *
	 * @param userDTO the user attempting the action (must be admin)
	 * @param title   the title of the CD
	 * @param artist  the artist of the CD
	 * @return the newly created {@link CD}
	 * @throws PermissionDeniedException if the user is not an admin
	 * @throws IllegalArgumentException  if {@link CD} validation fails
	 * @throws IllegalStateException     if the CD could not be added to the
	 *                                   repository
	 */
	public CD addCD(UserDTO userDTO, String title, String artist)
			throws PermissionDeniedException, IllegalStateException, IllegalArgumentException {

		AuthorizationService.ensureAdmin(userDTO);

		CD cd = new CD(title, artist);

		boolean added = cdRepo.addCD(cd);
		if (!added) {
			throw new IllegalStateException("Failed to add CD: " + title + " by " + artist);
		}

		return cd;
	}

	/**
	 * Adds a new CD to the system with a specified number of copies, if the requesting user has admin privileges.
	 *
	 * @param userDTO the user attempting the action (must be admin)
	 * @param title   the title of the CD
	 * @param artist  the artist of the CD
	 * @param totalCopies the total number of copies owned by the library
	 * @return the newly created {@link CD}
	 * @throws PermissionDeniedException if the user is not an admin
	 * @throws IllegalArgumentException  if {@link CD} validation fails
	 * @throws IllegalStateException     if the CD could not be added to the repository
	 */
	public CD addCD(UserDTO userDTO, String title, String artist, int totalCopies)
			throws PermissionDeniedException, IllegalStateException, IllegalArgumentException {

		AuthorizationService.ensureAdmin(userDTO);

		CD cd = new CD(title, artist, totalCopies);

		boolean added = cdRepo.addCD(cd);
		if (!added) {
			throw new IllegalStateException("Failed to add CD: " + title + " by " + artist);
		}

		return cd;
	}

	/**
	 * Retrieves all CDs currently in the repository.
	 *
	 * @return a list of all {@link CD} entities
	 */
	public List<CD> getAllCDs() {
		return cdRepo.getAllCDs();
	}

	/**
	 * Retrieves a CD by its unique identifier.
	 *
	 * @param cdId the UUID of the CD
	 * @return the {@link CD} if found
	 * @throws IllegalArgumentException if the CD ID is not found
	 */
	public CD getCDById(UUID cdId) {
		return cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException("CD not found with ID: " + cdId));
	}

	/**
	 * Updates an existing CD's information, if the requesting user has admin
	 * privileges.
	 *
	 * <p>
	 * This method performs the following steps:
	 * </p>
	 * <ol>
	 * <li>Verifies that the given user is an administrator.</li>
	 * <li>Retrieves the existing CD from the repository.</li>
	 * <li>Updates only the fields that are not null, with validation.</li>
	 * <li>Persists the updated CD using {@link CDRepository}.</li>
	 * </ol>
	 *
	 * @param userDTO   the user attempting the action (must be admin)
	 * @param cdId      the UUID of the CD to update
	 * @param newTitle  the new title (if null, keeps existing)
	 * @param newArtist the new artist (if null, keeps existing)
	 * @param newTotalCopies the new total copies (if null, keeps existing)
	 * @return the updated {@link CD}
	 * @throws PermissionDeniedException if the user is not an admin
	 * @throws IllegalArgumentException  if the CD is not found or validation fails
	 * @throws IllegalStateException     if the update fails
	 */
	public CD updateCD(UserDTO userDTO, UUID cdId, String newTitle, String newArtist, Integer newTotalCopies)
			throws PermissionDeniedException, IllegalArgumentException, IllegalStateException {

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
				throw new IllegalArgumentException(
					"New total copies (" + newTotalCopies + ") cannot be less than available copies (" 
					+ cd.getAvailableCopies() + ")");
			}
			cd.setTotalCopies(newTotalCopies);
		}

		boolean updated = cdRepo.updateCD(cd);
		if (!updated) {
			throw new IllegalStateException("Failed to update CD with ID: " + cdId);
		}

		return cd;
	}

	/**
	 * Deletes a CD from the system, if the requesting user has admin privileges.
	 *
	 * @param userDTO the user attempting the action (must be admin)
	 * @param cdId    the UUID of the CD to delete
	 * @throws PermissionDeniedException if the user is not an admin
	 * @throws IllegalArgumentException  if the CD is not found
	 */
	public void deleteCD(UserDTO userDTO, UUID cdId) throws PermissionDeniedException, IllegalArgumentException {

		AuthorizationService.ensureAdmin(userDTO);

		boolean deleted = cdRepo.deleteCD(cdId);
		if (!deleted) {
			throw new IllegalArgumentException("CD not found with ID: " + cdId);
		}
	}

	/**
	 * Searches for CDs by keyword (matches title or artist).
	 *
	 * @param keyword the search keyword
	 * @return a list of matching {@link CD} entities
	 */
	public List<CD> searchCDs(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return getAllCDs();
		}
		return cdRepo.searchCDs(keyword);
	}

	/**
	 * Searches for CDs using a specific search strategy.
	 *
	 * @param strategy the search strategy to apply
	 * @param searchTerm the search term
	 * @return a list of matching {@link CD} entities
	 */
	public List<CD> searchCDs(lms.application.search.SearchStrategy<CD> strategy, String searchTerm) {
		if (strategy == null) {
			throw new IllegalArgumentException("Search strategy cannot be null");
		}
		return strategy.execute(cdRepo.getAllCDs(), searchTerm);
	}

	/**
	 * Checks if a CD is available (not borrowed).
	 *
	 * @param cdId the UUID of the CD
	 * @return true if the CD exists and is not borrowed, false otherwise
	 */
	public boolean isAvailableCD(UUID cdId) {
		return cdRepo.getCDById(cdId).map(cd -> !cd.isBorrowed()).orElse(false);
	}

	/**
	 * Validates if a CD exists in the repository.
	 *
	 * @param cdId the UUID of the CD
	 * @return true if the CD exists, false otherwise
	 */
	public boolean isValidCD(UUID cdId) {
		return cdRepo.getCDById(cdId).isPresent();
	}

	/**
	 * Marks a CD as borrowed.
	 *
	 * @param userDTO the user attempting the action
	 * @param cdId    the UUID of the CD to borrow
	 * @throws IllegalArgumentException if the CD is not found
	 * @throws IllegalStateException    if the CD is already borrowed
	 */
	public void borrowCD(UserDTO userDTO, UUID cdId) throws IllegalArgumentException, IllegalStateException {

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

	/**
	 * Marks a CD as returned.
	 *
	 * @param userDTO the user attempting the action
	 * @param cdId    the UUID of the CD to return
	 * @throws IllegalArgumentException if the CD is not found
	 * @throws IllegalStateException    if the CD was not borrowed
	 */
	public void returnCD(UserDTO userDTO, UUID cdId) throws IllegalArgumentException, IllegalStateException {

		CD cd = cdRepo.getCDById(cdId).orElseThrow(() -> new IllegalArgumentException("CD not found with ID: " + cdId));

		if (cd.getAvailableCopies() >= cd.getTotalCopies()) {
			throw new IllegalStateException("All copies are already returned: " + cd.getTitle());
		}

		cd.incrementAvailableCopies();
		boolean updated = cdRepo.updateCD(cd);
		if (!updated) {
			throw new IllegalStateException("Failed to update CD return status");
		}
	}
}
