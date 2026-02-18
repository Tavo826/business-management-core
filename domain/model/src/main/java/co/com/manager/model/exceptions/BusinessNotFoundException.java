package co.com.manager.model.exceptions;

public class BusinessNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Negocio para cliente [id=%s] no encontrado";

    public BusinessNotFoundException(String id) {
        super(MESSAGE.formatted(id));
    }
}
