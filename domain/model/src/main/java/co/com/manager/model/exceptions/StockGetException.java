package co.com.manager.model.exceptions;

public class StockGetException extends RuntimeException {
    public StockGetException(String message) {
        super(message);
    }

    public StockGetException(String message, Throwable cause) {
        super(message, cause);
    }
}
