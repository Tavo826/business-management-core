package co.com.manager.model.exceptions;

public class BusinessNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Business [id=%s] not found";

    public BusinessNotFoundException(String id) {
        super(MESSAGE.formatted(id));
    }
}
