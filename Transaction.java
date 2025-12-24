import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents an immutable transaction record in a bank account.
 * 
 * <p>
 * Each transaction captures:
 * <ul>
 * <li>A unique identifier (UUID)</li>
 * <li>The transaction amount</li>
 * <li>The type of transaction (deposit, withdrawal, transfer, etc.)</li>
 * <li>The exact timestamp when the transaction was created</li>
 * </ul>
 * 
 * <p>
 * This class is immutable and thread-safe. Once created, a transaction
 * cannot be modified, ensuring audit trail integrity.
 * 
 * @author BankSystem Team
 * @version 1.0
 * @see TransactionType
 */
public final class Transaction {
    private final String id;
    private final double amount;
    private final TransactionType type;
    private final LocalDateTime timestamp;

    /**
     * Creates a new transaction with the specified amount and type.
     * 
     * <p>
     * The transaction ID is automatically generated using UUID, and the
     * timestamp is set to the current moment.
     * 
     * @param amount the transaction amount in dollars (can be any positive value)
     * @param type   the type of transaction (must not be null)
     * @throws NullPointerException if type is null
     */
    public Transaction(double amount, TransactionType type) {
        this.id = UUID.randomUUID().toString(); // Auto-generate a unique ID (e.g., "a1b2-c3d4...")
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now(); // Capture the exact moment of creation

    }

    /**
     * Returns the unique identifier for this transaction.
     * 
     * @return the transaction ID (UUID string)
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the amount of this transaction.
     * 
     * @return the transaction amount in dollars
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the type of this transaction.
     * 
     * @return the transaction type
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Returns the timestamp when this transaction was created.
     * 
     * @return the transaction timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        // Modern Java Date Formatting
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return String.format("[%s] %s: $%.2f (ID: %s)",
                timestamp.format(formatter), type, amount, id.substring(0, 8));
    }
}
