package todoapp;

/**
 * Custom exception for user-initiated cancellations
 * Thrown when user enters 'back' command during input operations
 * Extends RuntimeException for convenient flow control
 */
public class CancellationException extends RuntimeException {
    public CancellationException() {
        super("Operation cancelled by user");
    }
}
