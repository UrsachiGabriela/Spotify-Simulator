package spotify.utils.errorhandling;

public class InvalidEnumException extends RuntimeException {
    public InvalidEnumException(String message) {
        super(message);
    }
}
