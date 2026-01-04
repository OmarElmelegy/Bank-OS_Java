package banking.exceptions;

public class InvalidCredentialsException extends Exception{
     /**
     * Constructs a new InvalidCredentialsException with the specified detail message.
     * 
     * @param message the detail message explaining why the credentials are invalid
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
