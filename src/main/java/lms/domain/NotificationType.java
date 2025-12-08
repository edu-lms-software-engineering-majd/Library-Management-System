package lms.domain;

/**
 * Represents types of notifications sent to users.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public enum NotificationType {

	/** Loan is overdue */
	OVERDUE,
	
	/** Loan is due soon */
	DUE_SOON,
	
	/** Loan request approved */
	LOAN_APPROVED,
	
	/** Loan request rejected */
	LOAN_REJECTED,
	
	/** Item successfully returned */
	ITEM_RETURNED,
	
	/** General message */
	GENERAL
}


