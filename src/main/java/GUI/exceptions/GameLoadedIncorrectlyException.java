package GUI.exceptions;

public class GameLoadedIncorrectlyException extends RuntimeException {
    public GameLoadedIncorrectlyException(String message) {
        super(message);
    }
}
