package capstone.library.exceptions;

/**
 * Exceptions that can be thrown when input is Null
 */
public class MissingInputException extends RuntimeException {

    public MissingInputException(String message) {
        super(message);
    }
}
