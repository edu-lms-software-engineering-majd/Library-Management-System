package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class FineTransactionTest {

	private static final double SMALL_AMOUNT = 10.0;
	private static final double MEDIUM_AMOUNT = 50.0;
	private static final double LARGE_AMOUNT = 100.0;
	private static final double NEGATIVE_AMOUNT = -25.0;
	private static final String LATE_RETURN_DESCRIPTION = "Late return fee";
	private static final String PAYMENT_DESCRIPTION = "Fine payment";
	private static final String REFUND_DESCRIPTION = "Overcharge refund";

	@Test
	void givenValidParameters_whenCreateFineTransaction_thenFieldsAreInitializedCorrectly() {
		FineTransaction transaction = new FineTransaction(SMALL_AMOUNT, LATE_RETURN_DESCRIPTION, TransactionType.FINE);

		assertNotNull(transaction.getTransactionId());
		assertEquals(SMALL_AMOUNT, transaction.getAmount());
		assertEquals(LATE_RETURN_DESCRIPTION, transaction.getDescription());
		assertEquals(TransactionType.FINE, transaction.getType());
		assertNotNull(transaction.getTransactionDate());
		assertEquals(LocalDate.now(), transaction.getTransactionDate());
	}

	@Test
	void givenFineType_whenCreateTransaction_thenTypeIsFine() {
		FineTransaction transaction = new FineTransaction(MEDIUM_AMOUNT, LATE_RETURN_DESCRIPTION, TransactionType.FINE);

		assertEquals(TransactionType.FINE, transaction.getType());
	}

	@Test
	void givenPaymentType_whenCreateTransaction_thenTypeIsPayment() {
		FineTransaction transaction = new FineTransaction(MEDIUM_AMOUNT, PAYMENT_DESCRIPTION, TransactionType.PAYMENT);

		assertEquals(TransactionType.PAYMENT, transaction.getType());
	}



	@Test
	void givenPositiveAmount_whenGetSummary_thenContainsPlusSign() {
		FineTransaction transaction = new FineTransaction(SMALL_AMOUNT, LATE_RETURN_DESCRIPTION, TransactionType.FINE);

		String summary = transaction.getSummary();

		assertNotNull(summary);
		assertTrue(summary.contains("+"));
		assertTrue(summary.contains("10.00"));
		assertTrue(summary.contains(LATE_RETURN_DESCRIPTION));
	}

	@Test
	void givenNegativeAmount_whenGetSummary_thenContainsMinusSign() {
		FineTransaction transaction = new FineTransaction(NEGATIVE_AMOUNT, PAYMENT_DESCRIPTION, TransactionType.PAYMENT);

		String summary = transaction.getSummary();

		assertNotNull(summary);
		assertTrue(summary.contains("-"));
		assertTrue(summary.contains("25.00"));
		assertTrue(summary.contains(PAYMENT_DESCRIPTION));
	}

	@Test
	void givenZeroAmount_whenCreateTransaction_thenAmountIsZero() {
		FineTransaction transaction = new FineTransaction(0.0, "No charge", TransactionType.FINE);

		assertEquals(0.0, transaction.getAmount());
	}

	@Test
	void givenMultipleTransactions_whenCreated_thenEachHasUniqueId() {
		FineTransaction transaction1 = new FineTransaction(SMALL_AMOUNT, "Transaction 1", TransactionType.FINE);
		FineTransaction transaction2 = new FineTransaction(MEDIUM_AMOUNT, "Transaction 2", TransactionType.PAYMENT);
		FineTransaction transaction3 = new FineTransaction(LARGE_AMOUNT, "Transaction 3", TransactionType.FINE);

		assertNotNull(transaction1.getTransactionId());
		assertNotNull(transaction2.getTransactionId());
		assertNotNull(transaction3.getTransactionId());
		assertTrue(!transaction1.getTransactionId().equals(transaction2.getTransactionId()));
		assertTrue(!transaction2.getTransactionId().equals(transaction3.getTransactionId()));
		assertTrue(!transaction1.getTransactionId().equals(transaction3.getTransactionId()));
	}

	@Test
	void givenTransaction_whenGetTransactionDate_thenReturnsCurrentDate() {
		FineTransaction transaction = new FineTransaction(MEDIUM_AMOUNT, LATE_RETURN_DESCRIPTION, TransactionType.FINE);

		LocalDate transactionDate = transaction.getTransactionDate();

		assertNotNull(transactionDate);
		assertEquals(LocalDate.now(), transactionDate);
	}

	@Test
	void givenLargeAmount_whenGetSummary_thenFormatsCorrectly() {
		FineTransaction transaction = new FineTransaction(LARGE_AMOUNT, "Large fine", TransactionType.FINE);

		String summary = transaction.getSummary();

		assertTrue(summary.contains("100.00"));
		assertTrue(summary.contains("NIS"));
		assertTrue(summary.contains("Large fine"));
	}

	@Test
	void givenDecimalAmount_whenGetSummary_thenRoundsToTwoDecimalPlaces() {
		FineTransaction transaction = new FineTransaction(15.567, "Test fine", TransactionType.FINE);

		String summary = transaction.getSummary();

		assertTrue(summary.contains("15.57") || summary.contains("15.56"));
	}

	@Test
	void givenEmptyDescription_whenCreateTransaction_thenDescriptionIsEmpty() {
		FineTransaction transaction = new FineTransaction(SMALL_AMOUNT, "", TransactionType.FINE);

		assertEquals("", transaction.getDescription());
	}

	@Test
	void givenLongDescription_whenCreateTransaction_thenFullDescriptionIsStored() {
		String longDescription = "This is a very long description for a transaction that includes multiple reasons and explanations";
		FineTransaction transaction = new FineTransaction(MEDIUM_AMOUNT, longDescription, TransactionType.FINE);

		assertEquals(longDescription, transaction.getDescription());
	}

	@Test
	void givenFineTransaction_whenGetAmount_thenReturnsExactAmount() {
		double exactAmount = 37.99;
		FineTransaction transaction = new FineTransaction(exactAmount, LATE_RETURN_DESCRIPTION, TransactionType.FINE);

		assertEquals(exactAmount, transaction.getAmount());
	}

	@Test
	void givenPaymentTransaction_whenGetSummary_thenIncludesPaymentDescription() {
		FineTransaction transaction = new FineTransaction(50.0, PAYMENT_DESCRIPTION, TransactionType.PAYMENT);

		String summary = transaction.getSummary();

		assertTrue(summary.contains(PAYMENT_DESCRIPTION));
		assertTrue(summary.contains("50.00"));
	}

}
