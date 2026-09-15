package service;
import model.Product;
import util.InventoryException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Module 3: Reporting.
 * Generates low-stock alerts and exports a full inventory snapshot
 * to a CSV file for record-keeping.
 */
public class ReportService {

    private final ProductService productService = new ProductService();

    /** Returns only the products whose quantity is at or below their reorder level. */
    public List<Product> getLowStockProducts() throws InventoryException {
        return productService.getAllProducts().stream()
                .filter(Product::isLowStock).collect(Collectors.toList());
    }
