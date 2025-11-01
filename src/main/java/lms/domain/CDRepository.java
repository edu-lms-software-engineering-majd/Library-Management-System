<<<<<<< HEAD
package lms.domain;

public interface CDRepository {

	// TODO : write the appropriate method definitions
}
||||||| 7160386
=======
package lms.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing CD entities.
 * 
 * <p>
 * This interface defines the standard CRUD operations for CDs in the Library
 * Management System.
 * 
 * Implementations may store CDs in memory, a database, or any other storage
 * mechanism.
 * 
 * @author Ahmad
 * @version 1.0
 */
public interface CDRepository {

	/**
	 * Adds a new CD to the repository.
	 * 
	 * @param cd the CD to add
	 * @return true if the CD was added successfully, false otherwise
	 */
	boolean addCD(CD cd);

	/**
	 * Updates an existing CD's information.
	 * 
	 * @param cd the CD with updated information
	 * @return true if the CD exists and was updated, false otherwise
	 */
	boolean updateCD(CD cd);

	/**
	 * Deletes a CD by its unique identifier.
	 * 
	 * @param id the UUID of the CD to delete
	 * @return true if the CD was found and deleted, false otherwise
	 */
	boolean deleteCD(UUID id);

	/**
	 * Retrieves a CD by its unique identifier.
	 * 
	 * @param id the UUID of the CD
	 * @return an Optional containing the CD if found, or empty if not found
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
>>>>>>> ahmad-salameh
