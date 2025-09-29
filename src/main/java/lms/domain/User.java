package lms.domain;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public class User {

//	User Meta-Data
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	private String passwordHash;
	private BigInteger userID;
	private final LocalDate registrationDate;

//	User Role
	private final Role role;

//	User Borrowed Items
	private List<Loan> loans;

//	User Financial Account - fine and payment history
	private Account account;
	
	private User() {
		registrationDate = LocalDate.now();
		role = Role.ADMIN;
	}
}
