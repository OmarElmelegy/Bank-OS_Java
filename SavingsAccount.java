/**
 * A savings account that earns interest and requires a positive balance.
 * 
 * <p>
 * Savings accounts do not allow overdrafts - withdrawals are only permitted
 * if sufficient balance exists. Each account has a configurable interest rate
 * that is applied independently of the central bank's default rate.
 * 
 * <p>
 * Key features:
 * <ul>
 * <li>No overdraft protection - balance must remain positive</li>
 * <li>Account-specific interest rate</li>
 * <li>Suitable for saving money and earning interest</li>
 * </ul>
 * 
 * @author BankSystem Team
 * @version 1.0
 * @see BankAccount
 * @see CheckingAccount
 */
public class SavingsAccount extends BankAccount {

    private static final double DEFAULT_INTEREST_RATE = 0.02; // 2% annual
    private final double interestRate;

    /**
     * Creates a savings account with the default interest rate (2%).
     * 
     * @param id    unique account identifier
     * @param owner name of the account owner
     */
    public SavingsAccount(String id, String owner) {
        this(id, owner, DEFAULT_INTEREST_RATE);
    }

    /**
     * Creates a savings account with a custom interest rate.
     * 
     * @param id           unique account identifier
     * @param owner        name of the account owner
     * @param interestRate annual interest rate (must be between 0.0 and 1.0)
     * @throws IllegalArgumentException if interest rate is not between 0 and 1
     */
    public SavingsAccount(String id, String owner, double interestRate) {
        super(id, owner);
        if (interestRate < 0 || interestRate > 1.0) {
            throw new IllegalArgumentException("Interest rate must be between 0 and 1");
        }
        this.interestRate = interestRate;
    }

    /**
     * Returns the interest rate for this savings account.
     * 
     * @return the annual interest rate (between 0.0 and 1.0)
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Applies interest to this account using the account-specific rate.
     * 
     * <p>
     * Overrides the parent method to use this account's interest rate
     * instead of the central bank's default rate.
     */
    @Override
    public void applyInterest() {
        super.applyInterest(interestRate);
    }

    /**
     * Withdraws money from the account.
     * 
     * <p>
     * Withdrawals are only allowed if sufficient balance exists.
     * Overdrafts are not permitted.
     * 
     * @param amount the amount to withdraw (must be positive)
     * @throws InsufficientFundsException if balance is insufficient
     * @throws InvalidAmountException     if amount is zero or negative
     */
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        withdrawInternal(amount, TransactionType.WITHDRAWAL);
    }

    @Override
    protected void withdrawInternal(double amount, TransactionType type)
            throws InsufficientFundsException, InvalidAmountException {

        // Validate amount first
        super.withdrawInternal(amount, type);

        // Then check balance
        if (amount > this.getBalance()) {
            throw new InsufficientFundsException(
                    String.format("Insufficient funds. Attempted: $%.2f, Available: $%.2f",
                            amount, this.getBalance()));
        }

        // Finally perform withdrawal
        finalizeWithdrawal(amount, type);
    }

}