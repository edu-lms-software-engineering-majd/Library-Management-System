package lms.domain.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountValidatorTest {

	private AccountValidator validator;

	@BeforeEach
	void setUp() {
		validator = AccountValidator.getInstance();
	}

	@AfterEach
	void tearDown() {
		validator = null;
	}

	@Test
	void givenNegativeFineAmount_whenValidateFineAmount_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateFineAmount(-10.0);
		});

		assertEquals("Fine amount must be positive", exception.getMessage());
	}

	@Test
	void givenZeroFineAmount_whenValidateFineAmount_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateFineAmount(0.0);
		});

		assertEquals("Fine amount must be positive", exception.getMessage());
	}

	@Test
	void givenVerySmallNegativeFineAmount_whenValidateFineAmount_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateFineAmount(-0.01);
		});

		assertEquals("Fine amount must be positive", exception.getMessage());
	}

	@Test
	void givenPositiveFineAmount_whenValidateFineAmount_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateFineAmount(10.0);
		});
	}

	@Test
	void givenSmallPositiveFineAmount_whenValidateFineAmount_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateFineAmount(0.01);
		});
	}

	@Test
	void givenLargePositiveFineAmount_whenValidateFineAmount_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateFineAmount(1000.0);
		});
	}

	@Test
	void givenNegativePaymentAmount_whenValidatePaymentAmount_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validatePaymentAmount(-10.0, 50.0);
		});

		assertEquals("Amount must be positive", exception.getMessage());
	}

	@Test
	void givenZeroPaymentAmount_whenValidatePaymentAmount_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validatePaymentAmount(0.0, 50.0);
		});

		assertEquals("Amount must be positive", exception.getMessage());
	}

	@Test
	void givenPaymentExceedsTotalFines_whenValidatePaymentAmount_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validatePaymentAmount(100.0, 50.0);
		});

		assertEquals("Amount exceeds total fines", exception.getMessage());
	}

	@Test
	void givenPaymentSlightlyExceedsTotalFines_whenValidatePaymentAmount_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validatePaymentAmount(50.01, 50.0);
		});

		assertEquals("Amount exceeds total fines", exception.getMessage());
	}

	@Test
	void givenValidPaymentAmount_whenValidatePaymentAmount_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validatePaymentAmount(25.0, 50.0);
		});
	}

	@Test
	void givenPaymentEqualToTotalFines_whenValidatePaymentAmount_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validatePaymentAmount(50.0, 50.0);
		});
	}

	@Test
	void givenSmallPaymentAmount_whenValidatePaymentAmount_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validatePaymentAmount(0.01, 50.0);
		});
	}

	@Test
	void givenPaymentWithZeroTotalFines_whenValidatePaymentAmount_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validatePaymentAmount(10.0, 0.0);
		});

		assertEquals("Amount exceeds total fines", exception.getMessage());
	}

	@Test
	void givenLargePaymentForLargeFines_whenValidatePaymentAmount_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validatePaymentAmount(500.0, 1000.0);
		});
	}
}
