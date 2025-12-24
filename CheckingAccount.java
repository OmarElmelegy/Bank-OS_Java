/**
 * A checking account with overdraft protection.
 * 
 * <p>
 * Checking accounts allow the balance to become negative up to a specified
 * overdraft limit. When a withdrawal causes the balance to transition from
 * non-negative to negative, a flat overdraft fee is automatically charged.
 * 
 * <p>
 * Key features:
 * <ul>
 * <li>Overdraft protection up to a configurable limit</li>
 * <li>Flat fee charged when entering overdraft (not on subsequent
 * transactions)</li>
 * <li>No minimum balance requirement</li>
 * <li>Suitable for frequent transactions</li>
 * </ul>
 * 
 * @author BankSystem Team
 * @version 1.0
 * @see BankAccount
 * @see SavingsAccount
 */
public class CheckingAccount extends BankAccount {

    private final double overdraftLimit;

    private static final double OVERDRAFT_FEE = 35.0; // Flat fee
    private static final double DEFAULT_OVERDRAFT_LIMIT = 500.0;
    private static final double ZERO_BALANCE = 0.0;

    /**
     * Creates a checking account with a custom overdraft limit.
     * 
     * @param id             unique account identifier
     * @param owner          name of the account owner
     * @param overdraftLimit maximum negative balance allowed (must be non-negative)
     * @throws IllegalArgumentException if overdraft limit is negative
     */
    public CheckingAccount(String id, String owner, double overdraftLimit) {
        super(id, owner);
        if (overdraftLimit < ZERO_BALANCE) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative");
        }
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Returns the overdraft limit for this account.
     * 
     * @return the maximum negative balance allowed
     */
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    /**
     * Returns the flat overdraft fee amount.
     * 
     * @return the overdraft fee in dollars
     */
    public static double getOverdraftFee() {
        return OVERDRAFT_FEE;
    }

    /**
     * Creates a checking account with the default overdraft limit.
     * 
     * @param id    unique account identifier
     * @param owner name of the account owner
     */
    public CheckingAccount(String id, String owner) {
        this(id, owner, DEFAULT_OVERDRAFT_LIMIT);
    }

    /**
     * Withdraws money from the account.
     * 
     * <p>
     * If the withdrawal causes the balance to go negative, an overdraft fee
     * will be charged. The withdrawal will fail if it would exceed the overdraft
     * limit (including the potential fee).
     * 
     * @param amount the amount to withdraw (must be positive)
     * @throws InsufficientFundsException if withdrawal would exceed overdraft limit
     * @throws InvalidAmountException     if amount is zero or negative
     */
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException, AccountStatusException {
        withdrawInternal(amount, TransactionType.WITHDRAWAL);
    }

    @Override
    protected void withdrawInternal(double amount, TransactionType type)
            throws InsufficientFundsException, InvalidAmountException, AccountStatusException {

        // Validate amount first
        super.withdrawInternal(amount, type);

        // Check if balance will go negative
        boolean willGoNegative = this.getBalance() >= ZERO_BALANCE && (this.getBalance() - amount < ZERO_BALANCE);

        // Calculate final balance including potential fee
        double finalBalance = this.getBalance() - amount;
        if (willGoNegative) {
            finalBalance -= OVERDRAFT_FEE;
        }

        double feeAmount = willGoNegative ? OVERDRAFT_FEE : ZERO_BALANCE;

        // Check overdraft limit including fee
        if (finalBalance < -this.overdraftLimit) {
            throw new InsufficientFundsException(
                    String.format(
                            "Overdraft limit exceeded. Withdrawal: $%.2f, Fee: $%.2f, Available: $%.2f (Balance: $%.2f + Overdraft: $%.2f)",
                            amount,
                            feeAmount,
                            this.getBalance() + this.overdraftLimit - feeAmount,
                            this.getBalance(),
                            this.overdraftLimit));
        }

        // Track if balance was positive before withdrawal
        boolean wasNonNegative = this.getBalance() >= 0;

        // Perform withdrawal
        finalizeWithdrawal(amount, type);

        // Only charge fee if balance BECAME negative
        if (wasNonNegative && this.getBalance() < ZERO_BALANCE) {
            finalizeWithdrawal(OVERDRAFT_FEE, TransactionType.FEE);
            System.err.printf("OVERDRAFT FEE: $%.2f charged to account %s (went into overdraft)%n",
                    feeAmount, this.getAccountNumber());
        }
    }

    @Override
    public String toString() {
        return String.format("CheckingAccount[%s, owner=%s, balance=$%.2f, overdraftLimit=$%.2f]",
                getAccountNumber(), getOwner(), getBalance(), overdraftLimit);
    }
}
