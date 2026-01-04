import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import banking.accounts.SavingsAccount;
import banking.exceptions.AccountStatusException;
import banking.exceptions.InvalidAmountException;

/**
 * Test suite for SavingsAccount functionality.
 * Focuses on interest rates and interest application.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SavingsAccountTest {

    /** Tests savings account interest rate */
    @Test
    @Order(1)
    @DisplayName("Test savings account custom interest rate")
    public void testSavingsAccountInterestRate() {
        SavingsAccount account = new SavingsAccount("SAV001", "Carol White", 0.05);
        assertThat(account.getInterestRate()).isEqualTo(0.05);
    }

    /** Tests savings account default interest rate */
    @Test
    @Order(2)
    @DisplayName("Test savings account default interest rate")
    public void testSavingsAccountDefaultInterestRate() {
        SavingsAccount account = new SavingsAccount("SAV002", "David Lee");
        assertThat(account.getInterestRate()).isEqualTo(0.02); // 2% default
    }

    /** Tests interest application on savings account */
    @Test
    @Order(3)
    @DisplayName("Test interest application on savings account")
    public void testInterestApplication() throws InvalidAmountException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("SAV003", "Quentin King", 0.05);
        account.deposit(1000.00);

        account.applyInterest();

        // Balance should be 1000 + (1000 * 0.05) = 1050
        assertThat(account.getBalance()).isEqualTo(1050.00);
    }

    /** Tests interest not applied on frozen account */
    @Test
    @Order(4)
    @DisplayName("Test interest not applied on frozen account")
    public void testInterestNotAppliedOnFrozenAccount() throws InvalidAmountException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("SAV004", "Rachel Lee", 0.05);
        account.deposit(1000.00);
        account.freezeAccount("Temporary hold");

        org.junit.jupiter.api.Assertions.assertThrows(AccountStatusException.class, () -> {
            account.applyInterest();
        });
    }

    /** Tests interest application with zero balance */
    @Test
    @Order(5)
    @DisplayName("Test interest application with zero balance")
    public void testInterestApplicationZeroBalance() throws AccountStatusException {
        SavingsAccount account = new SavingsAccount("SAV005", "Sam Taylor", 0.05);
        account.applyInterest();
        assertThat(account.getBalance()).isEqualTo(0.0);
    }

    /** Tests multiple interest applications */
    @Test
    @Order(6)
    @DisplayName("Test multiple interest applications")
    public void testMultipleInterestApplications() throws InvalidAmountException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("SAV006", "Tina Brown", 0.10);
        account.deposit(100.00);

        account.applyInterest(); // 100 * 1.10 = 110
        account.applyInterest(); // 110 * 1.10 = 121

        assertThat(account.getBalance()).isEqualTo(121.00);
    }
}
