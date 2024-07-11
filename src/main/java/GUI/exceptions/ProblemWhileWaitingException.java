package GUI.exceptions;

public class ProblemWhileWaitingException extends RuntimeException{

    public ProblemWhileWaitingException(String message) {
        super(message);
    }

    public ProblemWhileWaitingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProblemWhileWaitingException(Throwable cause) {
        super(cause);
    }
}
