package exceptions;

public class DatabaseInitializationException extends RuntimeException {
    public DatabaseInitializationException(String message) {
        super(message);
    }
}