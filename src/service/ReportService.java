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

    /** Prints a formatted low-stock report to the console. */
    public void printLowStockReport() throws InventoryException {
        List<Product> low = getLowStockProducts();
        System.out.println("=== Low Stock Report ===");
        if (low.isEmpty()) {
            System.out.println("All products above reorder level.");
            return;
        }
        for (Product p : low) {
            System.out.printf("%-20s qty=%-5d reorder=%-5d%n", p.getName(), p.getQuantity(), p.getReorderLevel());
        }
    }

    /** Writes every product to a CSV file, with a LOW/OK status column for quick scanning. */
    public void exportReportToFile(String filePath) throws InventoryException {
        try (FileWriter w = new FileWriter(filePath)) {
            w.write("id,name,category,quantity,price,reorder_level,status\n");
            for (Product p : productService.getAllProducts()) {
                w.write(String.format("%d,%s,%s,%d,%.2f,%d,%s%n", p.getId(), p.getName(), p.getCategory(),
                        p.getQuantity(), p.getPrice(), p.getReorderLevel(), p.isLowStock() ? "LOW" : "OK"));
            }
        } catch (IOException e) {
            System.out.println("Failed to write report: " + e.getMessage());
        }
    }
}
