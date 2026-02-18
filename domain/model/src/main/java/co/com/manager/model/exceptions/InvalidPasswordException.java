package co.com.manager.model.exceptions;

public class InvalidPasswordException extends RuntimeException {

    private static final String MESSAGE = "Contraseña incorrecta";

    public InvalidPasswordException() {
        super(MESSAGE);
    }
}
