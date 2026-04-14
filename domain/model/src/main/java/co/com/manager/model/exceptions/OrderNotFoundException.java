package co.com.manager.model.exceptions;

public class OrderNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Órden para negocio no encontrado";

    public OrderNotFoundException(String name) {
        super(MESSAGE);
    }
}
