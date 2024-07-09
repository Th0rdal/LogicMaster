package GUI.exceptions;

public class GameLoopInterruptedException extends RuntimeException {
    public GameLoopInterruptedException(String s, Throwable cause) {
        super(s, cause);
    }
}
