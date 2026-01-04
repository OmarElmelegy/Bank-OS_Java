
package banking.accounts;

/**
 * Represents the operational status of a bank account.
 * 
 * <p>
 * The status determines what operations can be performed on an account:
 * <ul>
 * <li>{@link #ACTIVE} - All operations are permitted</li>
 * <li>{@link #FROZEN} - All financial operations are blocked pending
 * investigation</li>
 * <li>{@link #CLOSED} - Account is permanently closed, no operations
 * allowed</li>
 * </ul>
 * 
 * @author BankSystem Team
 * @version 1.0
 * @see BankAccount#getStatus()
 * @see BankAccount#freezeAccount(String)
 * @see BankAccount#closeAccount(String)
 */
public enum AccountStatus {
    /**
     * Account is active and all operations (deposits, withdrawals, transfers) are
     * permitted.
     */
    ACTIVE,

    /**
     * Account is temporarily frozen due to suspicious activity or investigation.
     * All financial operations are blocked until the account is unfrozen.
     */
    FROZEN,

    /**
     * Account is permanently closed. No operations are permitted.
     * Balance must be zero before an account can be closed.
     */
    CLOSED
}
