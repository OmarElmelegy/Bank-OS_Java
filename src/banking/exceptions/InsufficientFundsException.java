
package banking.exceptions;

/**
 * Exception thrown when an account has insufficient funds for a transaction.
 * 
 * <p>
 * This exception is thrown when:
 * <ul>
 * <li>A withdrawal would exceed the available balance</li>
 * <li>A transfer cannot be completed due to insufficient funds</li>
 * <li>An overdraft limit would be exceeded</li>
 * <li>A minimum balance requirement would be violated</li>
 * </ul>
 * 
 * @author BankSystem Team
 * @version 1.0
 */
public class InsufficientFundsException extends Exception {
    /**
     * Constructs a new InsufficientFundsException with the specified detail
     * message.
     * 
     * @param message the detail message explaining the insufficient funds condition
     */
    public InsufficientFundsException(String message) {
        super(message);
    }
}