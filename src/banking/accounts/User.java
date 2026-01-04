package banking.accounts;

import java.io.Serializable;

/**
 * Represents a user with credentials and a linked bank account.
 * 
 * <p>
 * Users authenticate with username/password and are linked to a single
 * bank account. This class is immutable and serializable for persistence.
 * 
 * <p>
 * Thread Safety: This class is immutable and therefore thread-safe.
 * 
 * @author BankSystem Team
 * @version 1.0
 */
public class User implements Serializable {
    private final String username;
    private final String password;
    private final String linkedAccountId;

    /**
     * Creates a new user with the specified credentials and linked account.
     * 
     * @param username        the unique username for authentication (must not be
     *                        null or empty)
     * @param password        the password for authentication (must not be null or
     *                        empty)
     * @param linkedAccountId the account number this user is associated with
     */
    public User(String username, String password, String linkedAccountId) {
        this.username = username;
        this.password = password;
        this.linkedAccountId = linkedAccountId;
    }

    /**
     * Returns the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password.
     * 
     * <p>
     * Note: In a production system, passwords should be hashed and never
     * stored or returned in plain text.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the account ID this user is linked to.
     * 
     * @return the linked account number
     */
    public String getLinkedAccountId() {
        return linkedAccountId;
    }
}
