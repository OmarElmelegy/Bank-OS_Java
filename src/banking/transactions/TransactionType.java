
package banking.transactions;

/**
 * Represents the type of a banking transaction.
 * 
 * <p>
 * This enum defines all possible transaction types that can occur
 * in a bank account.
 * 
 * @author BankSystem Team
 * @version 1.0
 */
public enum TransactionType {
    /** Money added to the account */
    DEPOSIT,

    /** Money removed from the account */
    WITHDRAWAL,

    /** Money transferred to another account */
    TRANSFER,

    /** Interest earned and added to the account */
    INTEREST,

    /** Fee charged to the account (e.g., overdraft fee) */
    FEE,

    /** Reversal of a failed transaction */
    REVERSAL
}