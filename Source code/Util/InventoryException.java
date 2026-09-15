package util;

/**
 * Single custom exception reused across the application for all
 * inventory-related error cases (insufficient stock, invalid product
 * data, product not found, database errors). The ErrorType enum
 * distinguishes the specific failure without needing a separate
 * exception class for each case.
 */
public class InventoryException extends Exception {

    public enum ErrorType {
        INSUFFICIENT_STOCK,
        INVALID_PRODUCT,
        PRODUCT_NOT_FOUND,
        DATABASE_ERROR
    }

    private final ErrorType errorType;

    public InventoryException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    /** Use this overload when wrapping a lower-level exception (e.g. SQLException). */
    public InventoryException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "InventoryException [" + errorType + "]: " + getMessage();
    }
}
