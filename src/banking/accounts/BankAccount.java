package banking.accounts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import banking.CentralBank;
import banking.exceptions.AccountStatusException;
import banking.exceptions.InsufficientFundsException;
import banking.exceptions.InvalidAmountException;
import banking.transactions.Transaction;
import banking.transactions.TransactionType;

/**
 * Abstract base class representing a bank account.
 * 
 * <p>
 * Provides common functionality for all types of bank accounts including:
 * <ul>
 * <li>Deposits and withdrawals with transaction logging</li>
 * <li>Money transfers between accounts</li>
 * <li>Interest application</li>
 * <li>Transaction history tracking</li>
 * </ul>
 * 
 * <p>
 * All monetary amounts are in dollars (USD). Subclasses must implement
 * {@link #withdrawInternal(double, TransactionType)} to define their specific
 * withdrawal rules (e.g., overdraft protection, minimum balance requirements).
 * 
 * <p>
 * Thread Safety: Deposit and transfer operations are synchronized.
 * 
 * @author BankSystem Team
 * @version 1.0
 * @see CheckingAccount
 * @see SavingsAccount
 */
public abstract class BankAccount implements Serializable {
    private final String accountNumber;
    private final String owner;
    private double balance;
    private AccountStatus status;
    protected final List<Transaction> transactionLog;
    private static final double ZERO_BALANCE = 0.0;

    // Use logging framework
    private static final Logger logger = Logger.getLogger(BankAccount.class.getName());

    /**
     * Constructs a new bank account with the specified account number and owner.
     * 
     * <p>
     * The account is created with an initial balance of zero and an empty
     * transaction log.
     * 
     * @param accountNumber the unique identifier for this account (must not be null
     *                      or empty)
     * @param owner         the name of the account own÷, AccountStatusExceptier
     *                      (must not be null or
     *                      empty)
     * @throws NullPointerException     if accountNumber or owner is null
     * @throws IllegalArgumentException if accountNumber or owner is empty after
     *                                  trimming
     */
    public BankAccount(String accountNumber, String owner) {
        this.accountNumber = Objects.requireNonNull(accountNumber, "Account number cannot be null");
        this.owner = Objects.requireNonNull(owner, "Owner cannot be null");

        if (accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        if (owner.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be empty");
        }

        this.status = AccountStatus.ACTIVE;
        this.balance = ZERO_BALANCE;
        this.transactionLog = new ArrayList<>();
    }

    /**
     * Returns the name of the account owner.
     * 
     * @return the owner's name
     */
    public String getOwnerName() {
        return this.owner;
    }

    /**
     * Returns the unique account number.
     * 
     * @return the account number
     */
    public String getAccountNumber() {
        return this.accountNumber;
    }

    /**
     * Returns the current balance of the account.
     * 
     * @return the current balance in dollars
     */
    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactionLog() {
        return Collections.unmodifiableList(transactionLog);
    }

    /**
     * Returns the current status of the account.
     * 
     * @return the account status (ACTIVE, FROZEN, or CLOSED)
     */
    public AccountStatus getStatus() {
        return status;
    }

    /**
     * Freezes the account, preventing all financial transactions.
     * 
     * <p>
     * When frozen, the account cannot perform deposits, withdrawals, or transfers.
     * The account can be unfrozen using {@link #unfreezeAccount(String)}.
     * 
     * @param reason the reason for freezing the account (e.g., "suspicious
     *               activity")
     */
    public void freezeAccount(String reason) {
        this.status = AccountStatus.FROZEN;
        logger.info("Account " + accountNumber + " frozen: " + reason);
    }

    /**
     * Unfreezes a previously frozen account, restoring it to active status.
     * 
     * <p>
     * After unfreezing, all normal account operations are permitted again.
     * 
     * @param reason the reason for unfreezing the account (e.g., "verification
     *               completed")
     */
    public void unfreezeAccount(String reason) {
        this.status = AccountStatus.ACTIVE;
        logger.info("Account " + accountNumber + " unfrozen: " + reason);
    }

    /**
     * Closes the account permanently.
     * 
     * <p>
     * The account balance must be exactly zero before closing. If the balance is
     * positive, all funds must be withdrawn first. If the balance is negative,
     * all debts must be paid off first.
     * 
     * <p>
     * Once closed, the account cannot be reopened or used for any transactions.
     * 
     * @param reason the reason for closing the account (e.g., "customer request")
     * @throws AccountStatusException if the account has a non-zero balance
     */
    public void closeAccount(String reason) throws AccountStatusException {
        if (this.balance > ZERO_BALANCE) {
            throw new AccountStatusException("Withdraw funds before closing.");
        }
        if (this.balance < ZERO_BALANCE) {
            throw new AccountStatusException("Pay off debts before closing.");
        }

        this.status = AccountStatus.CLOSED;
        logger.info("Account " + accountNumber + " closed: " + reason);
    }

    public synchronized void deposit(double amount)
            throws InvalidAmountException, AccountStatusException {
        deposit(amount, TransactionType.DEPOSIT);
    }

    private void deposit(double amount, TransactionType type) throws InvalidAmountException, AccountStatusException {
        if (this.status == AccountStatus.CLOSED) {
            throw new AccountStatusException("Account is closed.");
        }
        if (this.status == AccountStatus.FROZEN) {
            throw new AccountStatusException("Account is frozen: Contact Support");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive.");
        }

        this.balance += amount;
        Transaction tempTransaction = new Transaction(amount, type);
        transactionLog.add(tempTransaction);

        // Use logging framework instead of console output
        logger.info(String.format("%s: $%.2f (New Balance: $%.2f)", type, amount, this.balance));
    }

    protected synchronized void withdrawInternal(double amount, TransactionType type)
            throws InsufficientFundsException, InvalidAmountException, AccountStatusException {
        if (this.status == AccountStatus.CLOSED) {
            throw new AccountStatusException("Account is closed");
        }
        if (this.status == AccountStatus.FROZEN) {
            throw new AccountStatusException("Account is frozen: Contact Support");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive.");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive.");
        }
    }

    protected final void finalizeWithdrawal(double amount, TransactionType type) {
        this.balance -= amount;
        Transaction tempTransaction = new Transaction(amount, type);
        transactionLog.add(tempTransaction);

        // Use logging framework instead of console output
        logger.info(String.format("%s: $%.2f (New Balance: $%.2f)", type, amount, this.balance));
    }

    /**
     * Prints a complete account statement to standard output.
     * 
     * <p>
     * The statement includes:
     * <ul>
     * <li>Account owner and number</li>
     * <li>Complete transaction history with timestamps</li>
     * <li>Current account balance</li>
     * </ul>
     */
    public void printStatement() {
        System.out.println("\n--- STATEMENT FOR: " + owner + " (" + accountNumber + ") ---");

        for (Transaction transaction : transactionLog) {
            System.out.println(transaction.toString());
        }

        System.out.println("CURRENT BALANCE: $" + this.balance + "\n");
    }

    /**
     * Transfers money from this account to another account.
     * 
     * <p>
     * This operation uses a two-phase commit with automatic rollback:
     * <ol>
     * <li>Withdraws the amount from this account</li>
     * <li>Deposits the amount into the target account</li>
     * <li>If deposit fails, automatically returns funds to this account</li>
     * </ol>
     * 
     * <p>
     * Thread-safe: This method is synchronized to prevent concurrent transfers.
     * 
     * @param target the destination account (must be active and not the same as
     *               this account)
     * @param amount the amount to transfer (must be positive)
     * @throws InvalidAmountException     if amount is not positive
     * @throws InsufficientFundsException if this account has insufficient funds
     * @throws AccountStatusException     if either account is not active
     * @throws IllegalArgumentException   if target is the same as this account
     */
    public synchronized void transfer(BankAccount target, double amount)
            throws InvalidAmountException, InsufficientFundsException, AccountStatusException {

        // Validate before any state changes
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive");
        }
        if (target.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountStatusException("Target account is not active");
        }
        if (this.equals(target)) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        // Perform withdrawal
        this.withdrawInternal(amount, TransactionType.TRANSFER);

        try {
            // Attempt deposit
            target.deposit(amount, TransactionType.TRANSFER);
        } catch (Exception e) {
            // ROLLBACK: Re-deposit withdrawn amount
            try {
                this.deposit(amount, TransactionType.REVERSAL);
                logger.info(String.format(
                        "Transfer failed from %s to %s: %s. Amount returned to source account.",
                        this.accountNumber, target.getAccountNumber(), e.getMessage()));
                System.err.println("Transfer failed - amount returned to source account");
            } catch (Exception rollbackEx) {
                // This should never happen, but log it
                logger.info(String.format(
                        "CRITICAL: Rollback failed for transfer from %s to %s! Data inconsistency!",
                        this.accountNumber, target.getAccountNumber()));
                System.err.println("CRITICAL: Rollback failed! Data inconsistency!");
                throw new RuntimeException("Transfer rollback failed", rollbackEx);
            }
            throw new RuntimeException("Transfer failed: " + e.getMessage(), e);
        }
        System.out.printf("SUCCESS: Transferred $%.2f from %s to %s%n",
                amount, this.accountNumber, target.getAccountNumber());
    }

    /**
     * Retrieves all transactions of a specific type from the transaction log.
     * 
     * <p>
     * Returns an unmodifiable list containing only transactions matching the
     * specified type (e.g., all deposits, all withdrawals, all fees).
     * 
     * @param type the transaction type to filter by
     * @return an unmodifiable list of transactions of the specified type
     */
    public List<Transaction> getTransactionByType(TransactionType type) {
        return transactionLog.stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Applies interest to the account using the central bank's interest rate.
     * 
     * <p>
     * The interest is calculated as: balance × central bank rate. The resulting
     * amount is deposited as an INTEREST transaction. Interest is only applied
     * if the account is active and the calculated profit is positive.
     * 
     * <p>
     * Note: SavingsAccount overrides this to use account-specific rates.
     * 
     * @throws AccountStatusException if the account is not active
     */
    public void applyInterest() throws AccountStatusException {
        applyInterest(CentralBank.getInstance().getInterestRate());
    }

    /**
     * Applies interest to the account using a specific interest rate.
     * 
     * <p>
     * This protected method is used by subclasses to apply custom interest rates.
     * The interest is calculated as: balance × interestRate. If the result is
     * positive and the account is active, it's added as an INTEREST transaction.
     * 
     * @param interestRate the interest rate to apply (e.g., 0.05 for 5%)
     * @throws AccountStatusException if the account is not active
     */
    protected void applyInterest(double interestRate) throws AccountStatusException {
        double profit = this.balance * interestRate;

        if (profit <= 0) {
            logger.info("No interest applied (zero or negative profit)");
            return;
        }
        if (this.status != AccountStatus.ACTIVE) {
            throw new AccountStatusException("Interest not applied - account not active");
        }

        try {
            deposit(profit, TransactionType.INTEREST);
            System.out.printf("Interest applied: $%.2f (rate: %.2f%%)%n",
                    profit, interestRate * 100);
        } catch (Exception e) {
            logger.warning("Interest operation failed: " + e.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BankAccount))
            return false;
        BankAccount that = (BankAccount) o;
        return accountNumber.equals(that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return String.format("BankAccount[%s, owner=%s, balance=$%.2f, status=%s]",
                accountNumber, owner, balance, status);
    }
}
