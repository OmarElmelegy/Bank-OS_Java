import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import banking.accounts.CheckingAccount;
import banking.exceptions.AccountStatusException;
import banking.exceptions.InsufficientFundsException;
import banking.exceptions.InvalidAmountException;

/**
 * Test suite for CheckingAccount functionality.
 * Focuses on overdraft features and fee application.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CheckingAccountTest {

    /** Tests checking account with overdraft */
    @Test
    @Order(1)
    @DisplayName("Test checking account overdraft")
    public void testCheckingAccountOverdraft()
            throws InvalidAmountException, InsufficientFundsException, AccountStatusException {
        CheckingAccount account = new CheckingAccount("CHK001", "Alice Johnson", 500.0);
        account.deposit(100.00);
        account.withdraw(150.00);
        assertThat(account.getBalance()).isLessThan(0.0);
        assertThat(account.getBalance()).isGreaterThan(-500.0);
    }

    /** Tests checking account overdraft fee */
    @Test
    @Order(2)
    @DisplayName("Test checking account overdraft fee")
    public void testCheckingAccountOverdraftFee()
            throws InvalidAmountException, InsufficientFundsException, AccountStatusException {
        CheckingAccount account = new CheckingAccount("CHK002", "Bob Smith");
        account.deposit(50.00);
        double initialBalance = account.getBalance();
        account.withdraw(100.00);
        // Balance should be: 50 - 100 - 35 (fee) = -85
        assertThat(account.getBalance()).isEqualTo(initialBalance - 100.0 - CheckingAccount.getOverdraftFee());
    }

    /** Tests checking account creation with custom overdraft limit */
    @Test
    @Order(3)
    @DisplayName("Test checking account with custom overdraft limit")
    public void testCustomOverdraftLimit() {
        CheckingAccount account = new CheckingAccount("CHK003", "Carol White", 1000.0);
        assertThat(account.getOverdraftLimit()).isEqualTo(1000.0);
    }

    /** Tests checking account creation with default overdraft limit */
    @Test
    @Order(4)
    @DisplayName("Test checking account with default overdraft limit")
    public void testDefaultOverdraftLimit() {
        CheckingAccount account = new CheckingAccount("CHK004", "David Lee");
        assertThat(account.getOverdraftLimit()).isEqualTo(500.0);
    }

    /** Tests overdraft limit enforcement */
    @Test
    @Order(5)
    @DisplayName("Test overdraft limit enforcement")
    public void testOverdraftLimitEnforcement() throws InvalidAmountException, AccountStatusException {
        CheckingAccount account = new CheckingAccount("CHK005", "Emma Wilson", 200.0);
        account.deposit(50.00);

        // Try to withdraw more than balance + overdraft limit
        org.junit.jupiter.api.Assertions.assertThrows(InsufficientFundsException.class, () -> {
            account.withdraw(300.00); // 50 balance + 200 overdraft = 250 max
        });
    }
}
