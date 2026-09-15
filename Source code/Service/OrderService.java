package service;
import model.Product;
import util.DBConnection;
import util.InventoryException;
import util.InventoryException.ErrorType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Module 2: Stock Transaction Processing.
 * Records stock-in / stock-out orders and updates product quantity.
 * A ReentrantLock guards the read-check-update sequence so that when
 * multiple threads (e.g. simulated sales terminals) place orders on the
 * same product at the same time, they can't both pass a stock check
 * that only one of them should have passed - this is what keeps the
 * quantity from going negative under concurrent load.
 */
public class OrderService {
    private final ProductService productService = new ProductService();
    private final ReentrantLock lock = new ReentrantLock();
    /**
     * Processes a stock order (IN or OUT) for a given product.
     * OUT orders are rejected with InsufficientStockException if there
     * isn't enough quantity on hand. Thread-safe via the internal lock.
     */
    public void processOrder(int productId, int qty, String type) throws InventoryException {
        if (qty <= 0) throw new InventoryException(ErrorType.INVALID_PRODUCT, "Quantity must be positive");
        if (!type.equals("IN") && !type.equals("OUT"))
            throw new InventoryException(ErrorType.INVALID_PRODUCT, "Type must be IN or OUT");

