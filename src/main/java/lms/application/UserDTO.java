package lms.application;

import java.util.UUID;
import lms.domain.Role;

/**
 * Data Transfer Object (DTO) representing a user in a simplified form for use
 * in the application layer or for transferring data between layers.
 *
 * <p>
 * This record contains only essential, non-sensitive user information: unique
 * identifier, username, first and last name, and the user's role. It is
 * typically used to avoid exposing the full {@link lms.domain.User} entity
 * outside the domain layer, improving encapsulation and security.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * UserDTO dto = new UserDTO(user.getUserID(), user.getUsername(), user.getFirstName(), user.getLastName(),
 * 		user.getRole());
 * System.out.println(dto.username());
 * </pre>
 * 
 * @param userID    the unique identifier of the user
 * @param username  the login username of the user
 * @param firstName the user's first name
 * @param lastName  the user's last name
 * @param role      the role assigned to the user (e.g., ADMIN, MEMBER)
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public record UserDTO(UUID userID, String username, String firstName, String lastName, Role role) {
}