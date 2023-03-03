package spotify.utils.errorhandling.customexceptions;

public class UnprocessableContentException extends RuntimeException {
    public UnprocessableContentException(String message) {
        super(message);
    }
}
