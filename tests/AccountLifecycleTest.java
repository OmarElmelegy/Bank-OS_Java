import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import banking.accounts.AccountStatus;
import banking.accounts.BankAccount;
import banking.accounts.SavingsAccount;
import banking.exceptions.AccountStatusException;
import banking.exceptions.InvalidAmountException;

/**
 * Test suite for account lifecycle management.
 * Covers account status changes: freeze, unfreeze, close.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountLifecycleTest {

    /** Tests account status after creation */
    @Test
    @Order(1)
    @DisplayName("Test account status after creation")
    public void testAccountStatusAfterCreation() {
        BankAccount account = new SavingsAccount("ACC501", "Patricia Jones");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    /** Tests account freeze functionality */
    @Test
    @Order(2)
    @DisplayName("Test account freeze")
    public void testAccountFreeze() throws InvalidAmountException, AccountStatusException {
        BankAccount account = new SavingsAccount("ACC201", "Henry Adams");
        account.deposit(1000.00);

        account.freezeAccount("Suspicious activity");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.FROZEN);

        // Should not be able to deposit when frozen
        assertThrows(AccountStatusException.class, () -> {
            account.deposit(100.00);
        });
    }

    /** Tests account unfreeze functionality */
    @Test
    @Order(3)
    @DisplayName("Test account unfreeze")
    public void testAccountUnfreeze() throws InvalidAmountException, AccountStatusException {
        BankAccount account = new SavingsAccount("ACC202", "Irene Brooks");
        account.deposit(500.00);

        account.freezeAccount("Security check");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.FROZEN);

        account.unfreezeAccount("Verification completed");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);

        // Should be able to deposit after unfreezing
        account.deposit(200.00);
        assertThat(account.getBalance()).isEqualTo(700.00);
    }

    /** Tests account closure with zero balance */
    @Test
    @Order(4)
    @DisplayName("Test account closure with zero balance")
    public void testAccountClosureZeroBalance() throws AccountStatusException {
        BankAccount account = new SavingsAccount("ACC203", "Jack Carter");

        account.closeAccount("Customer request");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
    }

    /** Tests account closure with non-zero balance fails */
    @Test
    @Order(5)
    @DisplayName("Test account closure with non-zero balance fails")
    public void testAccountClosureNonZeroBalance() throws InvalidAmountException, AccountStatusException {
        BankAccount account = new SavingsAccount("ACC204", "Karen Davis");
        account.deposit(500.00);

        assertThrows(AccountStatusException.class, () -> {
            account.closeAccount("Attempted closure");
        });
    }

    /** Tests operations on closed account are rejected */
    @Test
    @Order(6)
    @DisplayName("Test operations on closed account rejected")
    public void testClosedAccountOperations() throws AccountStatusException {
        BankAccount account = new SavingsAccount("ACC205", "Laura Evans");
        account.closeAccount("Account closed");

        assertThrows(AccountStatusException.class, () -> {
            account.deposit(100.00);
        });
    }

    /** Tests transfer to frozen account fails with rollback */
    @Test
    @Order(7)
    @DisplayName("Test transfer to frozen account with rollback")
    public void testTransferToFrozenAccountRollback() throws InvalidAmountException, AccountStatusException {
        BankAccount source = new SavingsAccount("ACC301", "Michael Foster");
        BankAccount target = new SavingsAccount("ACC302", "Nancy Green");

        source.deposit(1000.00);
        target.deposit(500.00);
        target.freezeAccount("Security review");

        double initialSourceBalance = source.getBalance();

        assertThrows(AccountStatusException.class, () -> {
            source.transfer(target, 200.00);
        });

        // Verify rollback - source should still have original balance
        assertThat(source.getBalance()).isEqualTo(initialSourceBalance);
    }

    /** Tests withdrawal on frozen account rejected */
    @Test
    @Order(8)
    @DisplayName("Test withdrawal on frozen account rejected")
    public void testWithdrawalOnFrozenAccount() throws InvalidAmountException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC206", "Mary Thompson");
        account.deposit(1000.00);
        account.freezeAccount("Investigation");

        assertThrows(AccountStatusException.class, () -> {
            account.withdraw(100.00);
        });
    }

    /** Tests unfreeze on already active account */
    @Test
    @Order(9)
    @DisplayName("Test unfreeze on already active account")
    public void testUnfreezeActiveAccount() throws AccountStatusException {
        BankAccount account = new SavingsAccount("ACC207", "Nathan King");

        // Account is already active, unfreeze should work (idempotent)
        account.unfreezeAccount("Already active");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    /** Tests freeze on already frozen account */
    @Test
    @Order(10)
    @DisplayName("Test freeze on already frozen account")
    public void testFreezeAlreadyFrozenAccount() throws AccountStatusException {
        BankAccount account = new SavingsAccount("ACC208", "Olivia Martin");
        account.freezeAccount("First freeze");

        // Freeze again (idempotent)
        account.freezeAccount("Second freeze");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.FROZEN);
    }
}
