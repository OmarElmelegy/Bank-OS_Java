import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Test suite for the BankSystem application.
 * Tests both CheckingAccount and SavingsAccount implementations.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankSystemTest {

    /** Tests that a savings account can be created successfully */
    @Test
    @Order(0)
    @DisplayName("Test savings account creation")
    public void testAccountCreation() {
        BankAccount account = new SavingsAccount("ACC001", "John Doe");
        assertThat(account).isNotNull();
        assertThat(account.getAccountNumber()).isEqualTo("ACC001");
        assertThat(account.getOwner()).isEqualTo("John Doe");
        assertThat(account.getBalance()).isEqualTo(0.0);
    }

    /** Tests deposit with positive amount */
    @Test
    @Order(1)
    @DisplayName("Test deposit positive amount")
    public void testDepositPositiveAmount() throws InvalidAmountException, AccountStatusException {
        BankAccount account = new SavingsAccount("ACC002", "Jane Smith");
        account.deposit(500.00);
        assertThat(account.getBalance()).isEqualTo(500.00);
    }

    /** Tests that negative deposit amounts are rejected */
    @Test
    @Order(2)
    @DisplayName("Test deposit negative amount rejected")
    public void testDepositNegativeAmount() {
        BankAccount account = new SavingsAccount("ACC003", "Bob Johnson");
        assertThrows(InvalidAmountException.class, () -> {
            account.deposit(-100.00);
        });
    }

    /** Tests that zero deposit amounts are rejected */
    @Test
    @Order(3)
    @DisplayName("Test deposit zero amount rejected")
    public void testDepositZeroAmount() {
        BankAccount account = new SavingsAccount("ACC004", "Alice Brown");
        assertThrows(InvalidAmountException.class, () -> {
            account.deposit(0);
        });
    }

    /** Tests successful withdrawal */
    @Test
    @Order(4)
    @DisplayName("Test successful withdrawal")
    public void testSuccessfulWithdrawal()
            throws InvalidAmountException, InsufficientFundsException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC005", "Charlie Davis");
        account.deposit(1000.00);
        account.withdraw(300.00);
        assertThat(account.getBalance()).isEqualTo(700.00);
    }

    /** Tests withdrawal with insufficient balance */
    @Test
    @Order(5)
    @DisplayName("Test withdrawal with insufficient balance")
    public void testWithdrawalInsufficientBalance() throws InvalidAmountException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC006", "Diana Evans");
        account.deposit(200.00);
        assertThrows(InsufficientFundsException.class, () -> {
            account.withdraw(500.00);
        });
    }

    /** Tests that negative withdrawal amounts are rejected */
    @Test
    @Order(6)
    @DisplayName("Test withdrawal negative amount rejected")
    public void testWithdrawalNegativeAmount() throws InvalidAmountException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC007", "Eve Foster");
        account.deposit(500.00);
        assertThrows(InvalidAmountException.class, () -> {
            account.withdraw(-100.00);
        });
    }

    /** Tests multiple transactions on the same account */
    @Test
    @Order(7)
    @DisplayName("Test multiple transactions")
    public void testMultipleTransactions()
            throws InvalidAmountException, InsufficientFundsException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC008", "Frank Green");
        account.deposit(1000.00);
        account.withdraw(200.00);
        account.deposit(500.00);
        account.withdraw(100.00);
        assertThat(account.getBalance()).isEqualTo(1200.00);
    }

    /** Tests printing account statement */
    @Test
    @Order(8)
    @DisplayName("Test print statement")
    public void testPrintStatement() throws InvalidAmountException, InsufficientFundsException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC009", "Grace Hill");
        account.deposit(750.00);
        account.withdraw(150.00);
        account.deposit(250.00);
        account.printStatement();
        assertThat(account.getBalance()).isEqualTo(850.00);
    }

    /** Tests transaction immutability and correctness */
    @Test
    @Order(9)
    @DisplayName("Test transaction immutability")
    public void testTransactionImmutability() {
        Transaction t1 = new Transaction(100.00, TransactionType.DEPOSIT);
        assertThat(t1.getId()).isNotNull();
        assertThat(t1.getAmount()).isEqualTo(100.00);
        assertThat(t1.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(t1.getTimestamp()).isNotNull();
    }

    /** Tests that all TransactionType enum values are accessible */
    @Test
    @Order(10)
    @DisplayName("Test TransactionType enum values")
    public void testTransactionTypeEnum() {
        assertThat(TransactionType.DEPOSIT).isNotNull();
        assertThat(TransactionType.WITHDRAWAL).isNotNull();
        assertThat(TransactionType.TRANSFER).isNotNull();
    }

    /** Tests printing statement for empty account */
    @Test
    @Order(11)
    @DisplayName("Test empty account statement")
    public void testEmptyAccountStatement() {
        BankAccount account = new SavingsAccount("ACC012", "Ivan King");
        account.printStatement();
        assertThat(account.getBalance()).isEqualTo(0.0);
    }

    /** Tests checking account with overdraft */
    @Test
    @Order(12)
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
    @Order(13)
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

    /** Tests savings account interest rate */
    @Test
    @Order(14)
    @DisplayName("Test savings account custom interest rate")
    public void testSavingsAccountInterestRate() {
        SavingsAccount account = new SavingsAccount("SAV001", "Carol White", 0.05);
        assertThat(account.getInterestRate()).isEqualTo(0.05);
    }

    /** Tests account transfer functionality */
    @Test
    @Order(15)
    @DisplayName("Test transfer between accounts")
    public void testTransferBetweenAccounts()
            throws InvalidAmountException, InsufficientFundsException, AccountStatusException {
        BankAccount account1 = new SavingsAccount("ACC101", "David Lee");
        BankAccount account2 = new SavingsAccount("ACC102", "Emma Wilson");

        account1.deposit(1000.00);
        account2.deposit(500.00);

        account1.transferTo(account2, 300.00);

        assertThat(account1.getBalance()).isEqualTo(700.00);
        assertThat(account2.getBalance()).isEqualTo(800.00);
    }

    /** Tests transfer with insufficient funds */
    @Test
    @Order(16)
    @DisplayName("Test transfer with insufficient funds")
    public void testTransferInsufficientFunds() throws InvalidAmountException, AccountStatusException {
        BankAccount account1 = new SavingsAccount("ACC103", "Frank Miller");
        BankAccount account2 = new SavingsAccount("ACC104", "Grace Taylor");

        account1.deposit(100.00);

        assertThrows(Exception.class, () -> {
            account1.transferTo(account2, 500.00);
        });
    }

    /** Tests account freeze functionality */
    @Test
    @Order(17)
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
    @Order(18)
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
    @Order(19)
    @DisplayName("Test account closure with zero balance")
    public void testAccountClosureZeroBalance() throws AccountStatusException {
        BankAccount account = new SavingsAccount("ACC203", "Jack Carter");

        account.closeAccount("Customer request");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
    }

    /** Tests account closure with non-zero balance fails */
    @Test
    @Order(20)
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
    @Order(21)
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
    @Order(22)
    @DisplayName("Test transfer to frozen account with rollback")
    public void testTransferToFrozenAccountRollback() throws InvalidAmountException, AccountStatusException {
        BankAccount source = new SavingsAccount("ACC301", "Michael Foster");
        BankAccount target = new SavingsAccount("ACC302", "Nancy Green");

        source.deposit(1000.00);
        target.deposit(500.00);
        target.freezeAccount("Security review");

        double initialSourceBalance = source.getBalance();

        assertThrows(AccountStatusException.class, () -> {
            source.transferTo(target, 200.00);
        });

        // Verify rollback - source should still have original balance
        assertThat(source.getBalance()).isEqualTo(initialSourceBalance);
    }

    /** Tests transaction log filtering by type */
    @Test
    @Order(23)
    @DisplayName("Test transaction filtering by type")
    public void testTransactionFiltering()
            throws InvalidAmountException, InsufficientFundsException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC401", "Oliver Hill");
        account.deposit(1000.00);
        account.withdraw(200.00);
        account.deposit(300.00);

        List<Transaction> deposits = account.getTransactionByType(TransactionType.DEPOSIT);
        List<Transaction> withdrawals = account.getTransactionByType(TransactionType.WITHDRAWAL);

        assertThat(deposits).hasSize(2);
        assertThat(withdrawals).hasSize(1);
    }

    /** Tests account status after creation */
    @Test
    @Order(24)
    @DisplayName("Test account status after creation")
    public void testAccountStatusAfterCreation() {
        BankAccount account = new SavingsAccount("ACC501", "Patricia Jones");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    /** Tests interest application on savings account */
    @Test
    @Order(25)
    @DisplayName("Test interest application on savings account")
    public void testInterestApplication() throws InvalidAmountException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC601", "Quentin King", 0.05);
        account.deposit(1000.00);

        account.applyInterest();

        // Balance should be 1000 + (1000 * 0.05) = 1050
        assertThat(account.getBalance()).isEqualTo(1050.00);
    }

    /** Tests interest not applied on frozen account */
    @Test
    @Order(26)
    @DisplayName("Test interest not applied on frozen account")
    public void testInterestNotAppliedOnFrozenAccount() throws InvalidAmountException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC602", "Rachel Lee", 0.05);
        account.deposit(1000.00);
        account.freezeAccount("Temporary hold");

        assertThrows(AccountStatusException.class, () -> {
            account.applyInterest();
        });
    }
}