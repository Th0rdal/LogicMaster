package GUI.exceptions;

public class GamestateLoadingException extends RuntimeException {
    public GamestateLoadingException(String message) {
        super(message);
    }

    public GamestateLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public GamestateLoadingException(Throwable cause) {
        super(cause);
    }
}
