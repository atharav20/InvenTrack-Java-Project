import model.Product;
import service.OrderService;
import service.ProductService;
import service.ReportService;
import util.InventoryException;

import java.util.List;
import java.util.Scanner;

/** CLI entry point for InvenTrack. Ties the three service modules together. */
public class Main {

    static ProductService ps = new ProductService();
    static OrderService os = new OrderService();
    static ReportService rs = new ReportService();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n1.Add 2.List 3.Order 4.Report 5.Export 6.Exit");
            System.out.print("Choose: ");
            String c = sc.nextLine().trim();
            try {
                if (c.equals("1")) addProduct();
                else if (c.equals("2")) listProducts();
                else if (c.equals("3")) recordOrder();
                else if (c.equals("4")) rs.printLowStockReport();
                else if (c.equals("5")) exportReport();
                else if (c.equals("6")) { System.out.println("Goodbye."); return; }
                else System.out.println("Invalid option.");
            } catch (InventoryException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a valid number.");
            }
        }
    }

    /** Prompts for and adds a new product. */
    static void addProduct() throws InventoryException {
        System.out.print("Name: "); String n = sc.nextLine();
        System.out.print("Category: "); String cat = sc.nextLine();
        System.out.print("Quantity: "); int q = Integer.parseInt(sc.nextLine());
        System.out.print("Price: "); double p = Double.parseDouble(sc.nextLine());
        System.out.print("Reorder level: "); int r = Integer.parseInt(sc.nextLine());
        ps.addProduct(new Product(n, cat, q, p, r));
        System.out.println("Product added.");
    }

    /** Lists every product currently stored. */
    static void listProducts() throws InventoryException {
        List<Product> all = ps.getAllProducts();
        if (all.isEmpty()) { System.out.println("No products yet."); return; }
        for (Product p : all)
            System.out.printf("[%d] %-15s qty=%-5d price=%.2f%n", p.getId(), p.getName(), p.getQuantity(), p.getPrice());
    }

    /** Prompts for and records a stock-in or stock-out order. */
    static void recordOrder() throws InventoryException {
        System.out.print("Product id: "); int id = Integer.parseInt(sc.nextLine());
        System.out.print("Quantity: "); int q = Integer.parseInt(sc.nextLine());
        System.out.print("Type (IN/OUT): "); String t = sc.nextLine().trim().toUpperCase();
        os.processOrder(id, q, t);
        System.out.println("Order processed.");
    }

    /** Prompts for a file path and writes the full inventory report there. */
    static void exportReport() throws InventoryException {
        System.out.print("File path: "); String path = sc.nextLine();
        rs.exportReportToFile(path);
        System.out.println("Report exported to " + path);
    }
}