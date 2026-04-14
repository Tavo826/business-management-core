package co.com.manager.model.business;

public class BusinessContext {

    private static final ThreadLocal<Business> CURRENT_BUSINESS = new ThreadLocal<>();

    private BusinessContext() {}

    public static void set(Business business) {
        CURRENT_BUSINESS.set(business);
    }

    public static Business get() {
        return CURRENT_BUSINESS.get();
    }

    public static void clear() {
        CURRENT_BUSINESS.remove();
    }
}
