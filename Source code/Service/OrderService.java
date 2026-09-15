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
