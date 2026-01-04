import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import banking.accounts.SavingsAccount;
import banking.exceptions.AccountStatusException;
import banking.exceptions.InsufficientFundsException;
import banking.exceptions.InvalidAmountException;
import banking.transactions.Transaction;
import banking.transactions.TransactionType;

/**
 * Test suite for Transaction objects and transaction logging.
 * Covers transaction immutability, types, and filtering.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionTest {

    /** Tests transaction immutability and correctness */
    @Test
    @Order(1)
    @DisplayName("Test transaction immutability")
    public void testTransactionImmutability() {
        Transaction t1 = new Transaction(100.00, TransactionType.DEPOSIT);
        assertThat(t1.getAmount()).isEqualTo(100.00);
        assertThat(t1.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(t1.getTimestamp()).isNotNull();
    }

    /** Tests that all TransactionType enum values are accessible */
    @Test
    @Order(2)
    @DisplayName("Test TransactionType enum values")
    public void testTransactionTypeEnum() {
        assertThat(TransactionType.DEPOSIT).isNotNull();
        assertThat(TransactionType.WITHDRAWAL).isNotNull();
        assertThat(TransactionType.TRANSFER).isNotNull();
        assertThat(TransactionType.FEE).isNotNull();
        assertThat(TransactionType.INTEREST).isNotNull();
        assertThat(TransactionType.REVERSAL).isNotNull();
    }

    /** Tests transaction log filtering by type */
    @Test
    @Order(3)
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

    /** Tests transaction log completeness */
    @Test
    @Order(4)
    @DisplayName("Test transaction log completeness")
    public void testTransactionLogCompleteness()
            throws InvalidAmountException, InsufficientFundsException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC402", "Patricia Brown");
        account.deposit(500.00);
        account.withdraw(100.00);
        account.deposit(200.00);

        List<Transaction> allTransactions = account.getTransactionLog();
        assertThat(allTransactions).hasSize(3);
    }

    /** Tests transaction timestamps are sequential */
    @Test
    @Order(5)
    @DisplayName("Test transaction timestamps are sequential")
    public void testTransactionTimestampsSequential() throws InvalidAmountException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC403", "Quinn Anderson");
        account.deposit(100.00);
        account.deposit(200.00);

        List<Transaction> transactions = account.getTransactionLog();
        assertThat(transactions.get(0).getTimestamp())
                .isAtMost(transactions.get(1).getTimestamp());
    }

    /** Tests different transaction types are created correctly */
    @Test
    @Order(6)
    @DisplayName("Test different transaction types")
    public void testDifferentTransactionTypes() {
        Transaction deposit = new Transaction(100.00, TransactionType.DEPOSIT);
        Transaction withdrawal = new Transaction(50.00, TransactionType.WITHDRAWAL);
        Transaction transfer = new Transaction(75.00, TransactionType.TRANSFER);
        Transaction fee = new Transaction(35.00, TransactionType.FEE);

        assertThat(deposit.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(withdrawal.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(transfer.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(fee.getType()).isEqualTo(TransactionType.FEE);
    }

    /** Tests transaction amount precision */
    @Test
    @Order(7)
    @DisplayName("Test transaction amount precision")
    public void testTransactionAmountPrecision() {
        Transaction t = new Transaction(123.456, TransactionType.DEPOSIT);
        assertThat(t.getAmount()).isWithin(0.001).of(123.456);
    }

    /** Tests filtering returns empty list when no matches */
    @Test
    @Order(8)
    @DisplayName("Test filtering with no matches")
    public void testFilteringNoMatches() throws InvalidAmountException, AccountStatusException {
        SavingsAccount account = new SavingsAccount("ACC404", "Ryan Clark");
        account.deposit(100.00);

        List<Transaction> withdrawals = account.getTransactionByType(TransactionType.WITHDRAWAL);
        assertThat(withdrawals).isEmpty();
    }
}
