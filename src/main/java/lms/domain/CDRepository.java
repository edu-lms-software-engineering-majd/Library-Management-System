package lms.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing CD entities.
 * 
 * <p>Defines CRUD operations for CDs in the library.</p>
 * 
 * @author Ahmad Salameh
 * @version 1.0
 */
public interface CDRepository {

	/**
	 * Adds a new CD.
	 * 
	 * @param cd the CD to add
	 * @return true if added, false otherwise
	 */
	boolean addCD(CD cd);

	/**
	 * Updates an existing CD.
	 * 
	 * @param cd the CD with updated information
	 * @return true if updated, false otherwise
	 */
	boolean updateCD(CD cd);

	/**
	 * Deletes a CD by ID.
	 * 
	 * @param id the CD ID
	 * @return true if deleted, false otherwise
	 */
	boolean deleteCD(UUID id);

	/**
	 * Retrieves a CD by ID.
	 * 
	 * @param id the CD ID
	 * @return Optional containing the CD if found
	 */
	Optional<CD> getCDById(UUID id);

	/**
	 * Retrieves all CDs available in the repository.
	 * 
	 * @return a list of all CDs
	 */
	List<CD> getAllCDs();

	/**
	 * Searches CDs by title or author.
	 * 
	 * @param keyword the keyword to match against title or author
	 * @return list of matching CDs
	 */
	List<CD> searchCDs(String keyword);
}
