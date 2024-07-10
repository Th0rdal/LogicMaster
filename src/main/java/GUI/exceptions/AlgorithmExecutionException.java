package GUI.exceptions;

public class AlgorithmExecutionException extends RuntimeException {

    public AlgorithmExecutionException(String message) {
        super(message);
    }

    public AlgorithmExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlgorithmExecutionException(Throwable cause) {
        super(cause);
    }

}
