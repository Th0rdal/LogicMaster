package GUI.exceptions;

public class ObjectInterruptedException extends RuntimeException {
    public ObjectInterruptedException(String message) {
        super(message);
    }

    public ObjectInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectInterruptedException(Throwable cause) {
        super(cause);
    }
}
