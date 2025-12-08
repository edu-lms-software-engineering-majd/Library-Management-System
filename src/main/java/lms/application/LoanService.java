package lms.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import lms.domain.Book;
import lms.domain.BookRepository;
import lms.domain.CD;
import lms.domain.CDRepository;
import lms.domain.Journal;
import lms.domain.JournalsRepository;
import lms.domain.Loan;
import lms.domain.LoanRepository;
import lms.domain.LoanableItem;
import lms.domain.User;
import lms.domain.UserRepository;
import lms.domain.exception.BorrowNotAllowedException;
import lms.domain.exception.ItemNotAvailableException;
import lms.domain.exception.ItemNotFoundException;
import lms.domain.exception.LoanAlreadyExistsException;
import lms.domain.exception.LoanNotFoundException;
import lms.domain.exception.PermissionDeniedException;
import lms.domain.exception.UserNotFoundException;

/**
 * Application service coordinating loan operations in the Library Management System.
 * 
 * <p>
 * This service manages the complete lifecycle of library item loans including:
 * </p>
 * <ul>
 * <li>Creating new loans (borrowing items)</li>
 * <li>Returning items and calculating fines</li>
 * <li>Extending loan periods</li>
 * <li>Checking for overdue loans and sending notifications</li>
 * <li>Managing loan status and history</li>
 * </ul>
 * 
 * <p><b>Key Features:</b></p>
 * <ul>
 * <li>Validates user borrowing eligibility (limits, fines, account status)</li>
 * <li>Manages item availability and copy counts</li>
 * <li>Automatic fine calculation for overdue items</li>
 * <li>Email and in-system notifications for overdue items</li>
 * <li>Support for multiple item types (books, CDs, journals)</li>
 * </ul>
 * 
 * <p>
 * The service uses {@link LoanServiceContext} to access all necessary repositories
 * and the notification service, following the Context pattern to reduce constructor
 * parameter count.
 * </p>
 * 
 * @author Majd Awwad
 * @version 3.0
 * @see Loan
 * @see LoanServiceContext
 * @see NotificationService
 */
public class LoanService {

	private static final Logger logger = Logger.getLogger(LoanService.class.getName());

	private final UserRepository userRepo;
	private final BookRepository bookRepo;
	private final CDRepository cdRepo;
	private final JournalsRepository journalRepo;
	private final LoanRepository loanRepo;
	private final NotificationService notificationService;

	/**
	 * Constructs a new LoanService with the required dependencies.
	 * 
	 * @param context the service context containing all required repositories and services
	 * @throws IllegalArgumentException if context is null
	 */
	public LoanService(LoanServiceContext context) {
		if (context == null) {
			throw new IllegalArgumentException("LoanServiceContext cannot be null");
		}
		RepositoryContext repos = context.getRepositories();
		this.userRepo = repos.getUserRepo();
		this.bookRepo = repos.getBookRepo();
		this.cdRepo = repos.getCdRepo();
		this.journalRepo = repos.getJournalRepo();
		this.loanRepo = repos.getLoanRepo();
		this.notificationService = context.getNotificationService();
	}

	/**
	 * Loans a library item to a user.
	 *
	 * <p>
	 * This method performs the following operations:
	 * </p>
	 * <ol>
	 * <li>Validates that the user exists and can borrow items</li>
	 * <li>Verifies that the requested item is available</li>
	 * <li>Creates a new loan record</li>
	 * <li>Updates the item's available copy count</li>
	 * <li>Updates the user's loan records</li>
	 * </ol>
	 *
	 * @param userDTO  the user data transfer object containing user information
	 * @param itemId   the unique identifier of the item to loan
	 * @param itemType the type of item (e.g., "book", "cd", "journal")
	 * @return the created Loan object
	 * @throws UserNotFoundException      if the user does not exist
	 * @throws ItemNotFoundException      if the item does not exist
	 * @throws ItemNotAvailableException  if the item is not available for borrowing
	 * @throws BorrowNotAllowedException  if the user has reached their borrowing
	 *                                    limit
	 * @throws LoanAlreadyExistsException
	 */
	public Loan loanItem(UserDTO userDTO, UUID itemId, String itemType) throws UserNotFoundException,
			ItemNotFoundException, ItemNotAvailableException, BorrowNotAllowedException, LoanAlreadyExistsException {

		User user = userRepo.getByID(userDTO.userID())
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userDTO.userID()));

		if (!user.canBorrow()) {
			throw new BorrowNotAllowedException("User has reached borrowing limit");
		}

		LoanableItem item = getItemByIdAndType(itemId, itemType);

		if (!item.isAvailable()) {
			throw new ItemNotAvailableException(itemType + " is not available for borrowing");
		}

		Loan loan = new Loan(user.getUserID(), itemId, itemType, LocalDate.now());

		item.decrementAvailableCopies();
		user.addLoan(loan);

		loanRepo.save(loan);
		updateItemRepository(item, itemType);
		userRepo.update(user);

		return loan;
	}

	/**
	 * Retrieves a loanable item by its ID and type.
	 *
	 * <p>
	 * This helper method queries the appropriate repository based on the item type
	 * and retrieves the corresponding item.
	 * </p>
	 *
	 * @param itemId   the unique identifier of the item
	 * @param itemType the type of item ("book", "cd", or "journal")
	 * @return the LoanableItem object
	 * @throws ItemNotFoundException    if the item is not found in the repository
	 * @throws IllegalArgumentException if the item type is not supported
	 */
	private LoanableItem getItemByIdAndType(UUID itemId, String itemType) throws ItemNotFoundException {

		return switch (itemType.toLowerCase()) {

		case "book" -> bookRepo.getBookById(itemId)
				.orElseThrow(() -> new ItemNotFoundException("Book not found with ID: " + itemId));

		case "cd" ->
			cdRepo.getCDById(itemId).orElseThrow(() -> new ItemNotFoundException("CD not found with ID: " + itemId));

		case "journal" -> journalRepo.getJournalById(itemId)
				.orElseThrow(() -> new ItemNotFoundException("Journal not found with ID: " + itemId));

		default -> throw new IllegalArgumentException("Unsupported item type: " + itemType);
		};
	}

	/**
	 * Updates the appropriate repository with the modified item.
	 *
	 * <p>
	 * This helper method delegates the update operation to the correct repository
	 * based on the item type.
	 * </p>
	 *
	 * @param item     the loanable item to update
	 * @param itemType the type of item ("book", "cd", or "journal")
	 */
	private void updateItemRepository(LoanableItem item, String itemType) {

		switch (itemType.toLowerCase()) {
		case "book" -> bookRepo.updateBook((Book) item);
		case "cd" -> cdRepo.updateCD((CD) item);
		case "journal" -> journalRepo.updateJournal((Journal) item);
		}
	}

	/**
	 * Processes the return of a loaned item.
	 *
	 * <p>
	 * This method handles the complete return process including:
	 * <ul>
	 * <li>Validating the loan exists and hasn't been returned</li>
	 * <li>Marking the loan as returned</li>
	 * <li>Incrementing the available copy count for the item</li>
	 * <li>Calculating and applying late fines if the item is overdue</li>
	 * <li>Updating all relevant repositories</li>
	 * </ul>
	 *
	 * @param userID the unique identifier of the user returning the item
	 * @param loanID the unique identifier of the loan to return
	 * @return true if the return operation is successful
	 * @throws IllegalArgumentException  if the loan does not exist
	 * @throws IllegalStateException     if the item has already been returned
	 * @throws UserNotFoundException     if the user associated with the loan is not
	 *                                   found
	 * @throws ItemNotFoundException     if the item associated with the loan is not
	 *                                   found
	 * @throws LoanNotFoundException
	 * @throws PermissionDeniedException
	 */
	public boolean returnItem(UUID userID, UUID loanID) throws IllegalArgumentException, IllegalStateException,
			UserNotFoundException, ItemNotFoundException, LoanNotFoundException, PermissionDeniedException {

		if (!AuthorizationService.isLibrarian(AuthService.getInstance().getCurrentUser())) {
			throw new IllegalStateException("Only librarians can process returns.");
		}

		UUID loanId = loanID;

		Loan loan = loanRepo.findById(loanID)
				.orElseThrow(() -> new LoanNotFoundException("Loan not found - ID: " + loanId));

		if (!loan.isActive()) {
			throw new IllegalStateException("Item already returned on: " + loan.getReturnDate());
		}

		loan.returnItem();

		LoanableItem item = getItemByIdAndType(loan.getItemId(), loan.getItemType());
		item.incrementAvailableCopies();

		User user = userRepo.getByID(loan.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

		double fineAmount = 0.0;
		if (loan.isOverdue()) {
			fineAmount = loan.calculateFine();
			user.getAccount().addFine(fineAmount,
					"Late return of " + loan.getItemType() + " - " + loan.getDaysOverdue() + " day(s) overdue");
			loan.markFineApplied();
			logger.warning("Late fine applied: " + fineAmount + " NIS to user '" + user.getUsername() + "' for " + loan.getItemType() + " '" + item.getTitle() + "'. Overdue by " + loan.getDaysOverdue() + " days");
		}

		loanRepo.update(loan);
		updateItemRepository(item, loan.getItemType());
		userRepo.update(user);

		notificationService.notifyItemReturned(user, item.getTitle(), loan.getItemType(), fineAmount);

		logger.info(loan.getItemType().toUpperCase() + " returned: '" + item.getTitle() + "' by user '" + user.getUsername() + "'" + (fineAmount > 0 ? " with fine " + fineAmount + " NIS" : ""));

		return true;
	}

	/**
	 * Checks for overdue loans and notifies users. This can be called by a
	 * scheduled task.
	 * 
	 * @throws ItemNotFoundException
	 */
	public void checkAndNotifyOverdueLoans() throws ItemNotFoundException {
		List<Loan> overdueLoans = loanRepo.findOverdueLoans();

		for (Loan loan : overdueLoans) {
			User user = userRepo.getByID(loan.getUserId()).orElse(null);
			if (user != null) {
				LoanableItem item = getItemByIdAndType(loan.getItemId(), loan.getItemType());
				notificationService.notifyOverdueItem(user, item.getTitle());
			}
		}
	}

	/**
	 * Retrieves the list of active loans for a given user.
	 *
	 * @param userId the user ID
	 * @return list of active loans
	 * @throws UserNotFoundException
	 */
	public List<Loan> getUserActiveLoans(UUID userId) throws UserNotFoundException {
		return loanRepo.findActiveLoansByUser(userId);
	}

	/**
	 * Retrieves all loans (active and completed) for a given user.
	 *
	 * @param userId the user ID
	 * @return list of all user loans
	 * @throws UserNotFoundException
	 */
	public List<Loan> getUserAllLoans(UUID userId) throws UserNotFoundException {
		return loanRepo.findByUserId(userId);
	}

	/**
	 * Retrieves all overdue loans in the system.
	 *
	 * @return list of overdue loans
	 */
	public List<Loan> getOverdueLoans() {
		return loanRepo.findOverdueLoans();
	}

	/**
	 * Retrieves a specific loan by ID.
	 *
	 * @param loanId the loan ID
	 * @return the loan object
	 * @throws IllegalArgumentException if the loan does not exist
	 */
	public Loan getLoan(UUID loanId) {
		return loanRepo.findById(loanId)
				.orElseThrow(() -> new IllegalArgumentException("Loan not found - ID: " + loanId));
	}

	/**
	 * Retrieves loan details along with LoanableItem information.
	 *
	 * @param loanId the loan ID
	 * @return an array containing [Loan, LoanableItem]
	 * @throws IllegalArgumentException if the loan does not exist
	 * @throws ItemNotFoundException    if the item associated with the loan is not
	 *                                  found
	 */
	public Object[] getLoanWithItemInfo(UUID loanId) throws ItemNotFoundException {
		Loan loan = getLoan(loanId);
		LoanableItem item = getItemByIdAndType(loan.getItemId(), loan.getItemType());
		return new Object[] { loan, item };
	}

	/**
	 * Calculates the total amount of fines owed by a user.
	 *
	 * @param userId the user ID
	 * @return total fine amount
	 * @throws UserNotFoundException
	 */
	public double calculateUserTotalFines(UUID userId) throws UserNotFoundException {
		List<Loan> activeLoans = getUserActiveLoans(userId);
		return activeLoans.stream().filter(Loan::isOverdue).mapToDouble(Loan::calculateFine).sum();
	}

	/**
	 * Extends a loan period (admin only).
	 *
	 * @param userDTO        the user requesting the extension
	 * @param loanId         the loan ID
	 * @param additionalDays number of extra days to add
	 * @return true if the extension is successful
	 * @throws PermissionDeniedException if the user is not an admin
	 * @throws IllegalArgumentException  if the loan does not exist or additional
	 *                                   days is not positive
	 * @throws IllegalStateException     if the loan has already been completed
	 */
	public boolean extendLoan(UserDTO userDTO, UUID loanId, int additionalDays) throws PermissionDeniedException, ItemNotFoundException, UserNotFoundException {
		AuthorizationService.ensureAdmin(userDTO);

		if (additionalDays <= 0) {
			throw new IllegalArgumentException("Additional days must be positive");
		}

		Loan loan = getLoan(loanId);
		if (!loan.isActive()) {
			throw new IllegalStateException("Cannot extend a completed loan");
		}

		User user = userRepo.getByID(loan.getUserId())
				.orElseThrow(() -> new UserNotFoundException("User not found for loan"));
		LoanableItem item = getItemByIdAndType(loan.getItemId(), loan.getItemType());
		
		logger.info("Loan extended by " + additionalDays + " days for user '" + user.getUsername() + "', " + loan.getItemType() + " '" + item.getTitle() + "'. New due date: " + loan.getDueDate().plusDays(additionalDays));
		return true;
	}

	/**
	 * Retrieves loan statistics.
	 *
	 * @return an array containing [total loans, active loans, overdue loans]
	 */
	public int[] getLoanStatistics() {
		List<Loan> allLoans = loanRepo.findAll();
		int totalLoans = allLoans.size();
		int activeLoans = (int) allLoans.stream().filter(loan -> loan.isActive()).count();
		int overdueLoans = (int) allLoans.stream().filter(Loan::isOverdue).count();

		return new int[] { totalLoans, activeLoans, overdueLoans };
	}

	/**
	 * Retrieves active loans for a user with their corresponding book information.
	 *
	 * @param userId the user ID
	 * @return list of arrays containing [Loan, Book]
	 * @throws UserNotFoundException
	 */
	public List<Object[]> getUserActiveLoansWithBooks(UUID userId) throws UserNotFoundException {
		List<Loan> activeLoans = getUserActiveLoans(userId);
		List<Object[]> result = new ArrayList<>();

		for (Loan loan : activeLoans) {
			Book book = bookRepo.getBookById(loan.getItemId()).orElse(null);
			if (book != null) {
				result.add(new Object[] { loan, book });
			}
		}

		return result;
	}
}
