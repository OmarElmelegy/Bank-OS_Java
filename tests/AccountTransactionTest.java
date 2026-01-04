import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import banking.accounts.BankAccount;
import banking.accounts.SavingsAccount;
import banking.exceptions.AccountStatusException;
import banking.exceptions.InsufficientFundsException;
import banking.exceptions.InvalidAmountException;

/**
 * Test suite for account transaction operations.
 * Covers deposits, withdrawals, transfers, and statements.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountTransactionTest {

    /** Tests that a savings account can be created successfully */
    @Test
    @Order(1)
    @DisplayName("Test savings account creation")
    public void testAccountCreation() {
        BankAccount account = new SavingsAccount("ACC001", "John Doe");
        assertThat(account).isNotNull();
        assertThat(account.getAccountNumber()).isEqualTo("ACC001");
        assertThat(account.getOwnerName()).isEqualTo("John Doe");
        assertThat(account.getBalance()).isEqualTo(0.0);
    }

    /** Tests deposit with positive amount */
    @Test
    @Order(2)
    @DisplayName("Test deposit positive amount")
    public void testDepositPositiveAmount() throws InvalidAmountException, AccountStatusException {
        BankAccount account = new SavingsAccount("ACC002", "Jane Smith");
        account.deposit(500.00);
        assertThat(account.getBalance()).isEqualTo(500.00);
    }

    /** Tests that negative deposit amounts are rejected */
    @Test
    @Order(3)
    @DisplayName("Test deposit negative amount rejected")
    public void testDepositNegativeAmount() {
        BankAccount account = new SavingsAccount("ACC003", "Bob Johnson");
        assertThrows(InvalidAmountException.class, () -> {
            account.deposit(-100.00);
        });
    }

    /** Tests that zero deposit amounts are rejected */
    @Test
    @Order(4)
    @DisplayName("Test deposit zero amount rejected")
    public void testDepositZeroAmount() {
        BankAccount account = new SavingsAccount("ACC004", "Alice Brown");
        assertThrows(InvalidAmountException.class, () -> {
            account.deposit(0);
        });
    }

    /** Tests successful withdrawal */
    @Test
    @Order(5)
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
    @Order(6)
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
    @Order(7)
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
    @Order(8)
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
    @Order(9)
    @DisplayName("Test print statement")
    public void testPrintStatement() throws InvalidAmountException, InsufficientFundsException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC009", "Grace Hill");
        account.deposit(750.00);
        account.withdraw(150.00);
        account.deposit(250.00);
        account.printStatement();
        assertThat(account.getBalance()).isEqualTo(850.00);
    }

    /** Tests printing statement for empty account */
    @Test
    @Order(10)
    @DisplayName("Test empty account statement")
    public void testEmptyAccountStatement() {
        BankAccount account = new SavingsAccount("ACC012", "Ivan King");
        account.printStatement();
        assertThat(account.getBalance()).isEqualTo(0.0);
    }

    /** Tests account transfer functionality */
    @Test
    @Order(11)
    @DisplayName("Test transfer between accounts")
    public void testTransferBetweenAccounts()
            throws InvalidAmountException, InsufficientFundsException, AccountStatusException {
        BankAccount account1 = new SavingsAccount("ACC101", "David Lee");
        BankAccount account2 = new SavingsAccount("ACC102", "Emma Wilson");

        account1.deposit(1000.00);
        account2.deposit(500.00);

        account1.transfer(account2, 300.00);

        assertThat(account1.getBalance()).isEqualTo(700.00);
        assertThat(account2.getBalance()).isEqualTo(800.00);
    }

    /** Tests transfer with insufficient funds */
    @Test
    @Order(12)
    @DisplayName("Test transfer with insufficient funds")
    public void testTransferInsufficientFunds() throws InvalidAmountException, AccountStatusException {
        BankAccount account1 = new SavingsAccount("ACC103", "Frank Miller");
        BankAccount account2 = new SavingsAccount("ACC104", "Grace Taylor");

        account1.deposit(100.00);

        assertThrows(Exception.class, () -> {
            account1.transfer(account2, 500.00);
        });
    }

    /** Tests transfer with negative amount */
    @Test
    @Order(13)
    @DisplayName("Test transfer with negative amount")
    public void testTransferNegativeAmount() throws InvalidAmountException, AccountStatusException {
        BankAccount account1 = new SavingsAccount("ACC105", "Harry Wilson");
        BankAccount account2 = new SavingsAccount("ACC106", "Iris Johnson");

        account1.deposit(500.00);

        assertThrows(InvalidAmountException.class, () -> {
            account1.transfer(account2, -100.00);
        });
    }

    /** Tests transfer to same account is rejected */
    @Test
    @Order(14)
    @DisplayName("Test transfer to same account rejected")
    public void testTransferToSameAccount()
            throws InvalidAmountException, AccountStatusException {
        BankAccount account = new SavingsAccount("ACC107", "Jack Smith");
        account.deposit(500.00);

        // Transfer to same account should be rejected
        assertThrows(IllegalArgumentException.class, () -> {
            account.transfer(account, 100.00);
        });
    }
}
