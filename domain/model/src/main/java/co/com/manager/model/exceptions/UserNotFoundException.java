package co.com.manager.model.exceptions;

public class UserNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Usuario %s no encontrado";

    public UserNotFoundException(String id) {
        super(MESSAGE.formatted(id));
    }
}
