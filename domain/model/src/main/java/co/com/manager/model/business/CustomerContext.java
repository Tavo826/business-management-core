package co.com.manager.model.business;

public class CustomerContext {

    private static final ThreadLocal<String> CURRENT_CUSTOMER_PHONE = new ThreadLocal<>();

    private CustomerContext() {}

    public static void set(String phone) {
        CURRENT_CUSTOMER_PHONE.set(phone);
    }

    public static String get() {
        return CURRENT_CUSTOMER_PHONE.get();
    }

    public static void clear() {
        CURRENT_CUSTOMER_PHONE.remove();
    }
}