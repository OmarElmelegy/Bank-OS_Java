import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
public abstract class BankAccount {
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
     * @param owner         the name of the account owner (must not be null or
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
    public String getOwner() {
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
        return transactionLog;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void freezeAccount(String reason) {
        this.status = AccountStatus.FROZEN;
        logger.info("Account " + accountNumber + " frozen: " + reason);
    }

    public void unfreezeAccount(String reason) {
        this.status = AccountStatus.ACTIVE;
        logger.info("Account " + accountNumber + " unfrozen: " + reason);
    }

    public void closeAccount(String reason) throws AccountStatusException {
        if (this.balance > ZERO_BALANCE) {
            throw new AccountStatusException("Withdraw funds before closing");
        }
        if (this.balance < ZERO_BALANCE) {
            throw new AccountStatusException("Pay off debts before closing");
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
            throw new AccountStatusException("Account is closed");
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

        // Always log the action
        System.out.printf("%s: $%.2f (New Balance: $%.2f)%n",
                type, amount, this.balance);

        logger.info("Deposit successful. New Balance: $" + this.balance);
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
    }

    public void printStatement() {
        System.out.println("\n--- STATEMENT FOR: " + owner + " (" + accountNumber + ") ---");

        for (Transaction transaction : transactionLog) {
            System.out.println(transaction.toString());
        }

        System.out.println("CURRENT BALANCE: $" + this.balance + "\n");
    }

    public synchronized void transferTo(BankAccount target, double amount)
            throws InvalidAmountException, InsufficientFundsException, AccountStatusException {

        // Validate before any state changes
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive");
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
                logger.warning(String.format(
                        "Transfer failed from %s to %s: %s. Amount returned to source account.",
                        this.accountNumber, target.getAccountNumber(), e.getMessage()));
                System.err.println("Transfer failed - amount returned to source account");
            } catch (Exception rollbackEx) {
                // This should never happen, but log it
                logger.severe(String.format(
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

    public List<Transaction> getTransactionByType(TransactionType type) {
        return transactionLog.stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.toUnmodifiableList());
    }

    public void applyInterest() {
        applyInterest(CentralBank.getInstance().getInterestRate());
    }

    protected void applyInterest(double interestRate) {
        double profit = this.balance * interestRate;

        if (profit <= 0) {
            System.out.println("No interest applied (zero profit)");
            return;
        }

        try {
            deposit(profit, TransactionType.INTEREST);
            System.out.printf("Interest applied: $%.2f (rate: %.2f%%)%n",
                    profit, interestRate * 100);
        } catch (Exception e) {
            System.err.println("Interest operation failed: " + e.getMessage());
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
}
