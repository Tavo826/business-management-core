package co.com.manager.model.exceptions;

public class InvalidTokenException extends RuntimeException {

    private static final String MESSAGE = "Token inválido: ";

    public InvalidTokenException(String message) {
        super(MESSAGE + message);
    }
}
