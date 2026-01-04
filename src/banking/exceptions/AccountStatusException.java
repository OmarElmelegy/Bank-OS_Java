
package banking.exceptions;

import banking.accounts.AccountStatus;
import banking.accounts.BankAccount;

/**
 * Exception thrown when an operation is attempted on an account that is not
 * in an appropriate status for that operation.
 * 
 * <p>
 * This exception is thrown in the following scenarios:
 * <ul>
 * <li>Attempting financial operations on a frozen account</li>
 * <li>Attempting any operations on a closed account</li>
 * <li>Attempting to close an account with a non-zero balance</li>
 * <li>Attempting to open a duplicate account</li>
 * <li>Attempting to retrieve a non-existent account</li>
 * </ul>
 * 
 * @author BankSystem Team
 * @version 1.0
 * @see AccountStatus
 * @see BankAccount#freezeAccount(String)
 * @see BankAccount#closeAccount(String)
 */
public class AccountStatusException extends Exception {

    /**
     * Constructs a new AccountStatusException with the specified detail
     * message.
     * 
     * @param message the detail message explaining the Account Status condition
     */
    public AccountStatusException(String message) {
        super(message);
    }
}
