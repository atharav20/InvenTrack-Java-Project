package service;

import model.Product;
import util.InventoryException;
import util.InventoryException.ErrorType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private static final String ORDERS_FILE = "orders_db.csv";

    private final ProductService productService = new ProductService();
    private final ReentrantLock lock = new ReentrantLock();

    public void processOrder(int productId, int qty, String type) throws InventoryException {
        if (qty <= 0) throw new InventoryException(ErrorType.INVALID_PRODUCT, "Quantity must be positive");
        if (!type.equals("IN") && !type.equals("OUT"))
            throw new InventoryException(ErrorType.INVALID_PRODUCT, "Type must be IN or OUT");

        lock.lock();
        try {
            Product p = productService.getProductById(productId);
            if (type.equals("OUT") && p.getQuantity() < qty)
                throw new InventoryException(ErrorType.INSUFFICIENT_STOCK,
                        "Only " + p.getQuantity() + " in stock for " + p.getName());

            p.setQuantity(type.equals("OUT") ? p.getQuantity() - qty : p.getQuantity() + qty);
            productService.updateProduct(p);
            recordOrder(productId, qty, type);
        } finally {
            lock.unlock();
        }
    }

    /** Appends a line to orders_db.csv for audit/history purposes. */
    private void recordOrder(int productId, int qty, String type) throws InventoryException {
        File file = new File(ORDERS_FILE);
        boolean needsHeader = !file.exists();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ORDERS_FILE, true))) {
            if (needsHeader) {
                bw.write("product_id,quantity,order_type,timestamp\n");
            }
            String record = String.format("%d,%d,%s,%d%n", productId, qty, type, System.currentTimeMillis());
            bw.write(record);
        } catch (IOException e) {
            throw new InventoryException(ErrorType.DATABASE_ERROR, "Failed to record order: " + e.getMessage(), e);
        }
    }
}