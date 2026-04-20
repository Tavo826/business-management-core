package co.com.manager.model.exceptions;

public class OrderNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Orden para negocio no encontrado";

    public OrderNotFoundException(String name) {
        super(MESSAGE);
    }
}
