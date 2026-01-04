package banking.exceptions;

public class UserStatusException extends Exception{
    /**
     * Constructs a new UserStatusException with the specified detail message.
     * 
     * @param message the detail message explaining why the user status is invalid
     */
    public UserStatusException(String message) {
        super(message);
    }
}
