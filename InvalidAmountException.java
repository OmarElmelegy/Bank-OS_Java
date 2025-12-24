/**
 * Exception thrown when an invalid transaction amount is provided.
 * 
 * <p>
 * This exception is thrown when:
 * <ul>
 * <li>Attempting to deposit or withdraw zero or negative amounts</li>
 * <li>Providing NaN or infinite values</li>
 * <li>Any other invalid amount is detected</li>
 * </ul>
 * 
 * @author BankSystem Team
 * @version 1.0
 */
public class InvalidAmountException extends Exception {
    /**
     * Constructs a new InvalidAmountException with the specified detail message.
     * 
     * @param message the detail message explaining why the amount is invalid
     */
    public InvalidAmountException(String message) {
        super(message);
    }
}