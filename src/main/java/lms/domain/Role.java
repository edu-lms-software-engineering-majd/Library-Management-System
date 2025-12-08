package lms.domain;

/**
 * Represents user roles in the Library Management System.
 * 
 * <p>Roles determine access levels and permissions.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public enum Role {
	/** Regular library member with borrowing privileges */
	MEMBER,

	/** Administrator with full system access */
	ADMIN,

	/** Library staff managing resources and loans */
	LIBRARIAN
}
