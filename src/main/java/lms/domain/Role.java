package lms.domain;

/**
 * Represents the role of a user in the Library Management System.
 * 
 * <p>Roles determine the level of access and permissions for each user.</p>
 * 
 * <ul>
 *   <li>{@link #MEMBER} - Regular library user who can borrow books and access general features.</li>
 *   <li>{@link #ADMIN} - User with full administrative privileges, including managing users and system settings.</li>
 *   <li>{@link #LIBRARIAN} - User responsible for managing library resources, loans, and assisting members.</li>
 * </ul>
 * 
 * <p>This enum is used by the {@link User} class to assign and check user roles.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public enum Role {
    /** Regular library member */
    MEMBER,

    /** System administrator */
    ADMIN,

    /** Library staff responsible for managing resources */
    LIBRARIAN
}
