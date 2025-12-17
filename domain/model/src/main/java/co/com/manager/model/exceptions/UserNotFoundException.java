package co.com.manager.model.exceptions;

public class UserNotFoundException extends RuntimeException {

    private static final String MESSAGE = "User [id=%s] not found";;

    public UserNotFoundException(String id) {
        super(MESSAGE.formatted(id));
    }
}
