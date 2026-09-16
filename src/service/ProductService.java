package service;

import model.Product;
import util.InventoryException;
import util.InventoryException.ErrorType;
import java.io.*;
import java.util.*;

/**
 * Module 1: Product Management (CRUD).
 * Flat-file storage using a CSV file instead of a database, so the
 * project runs with zero external setup - no driver jar, no DB server.
 */
public class ProductService {
    private static final String FILE_NAME = "products_db.csv";
    private static int currentId = 1;
    private static final Object idLock = new Object();

    public ProductService() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        if (id >= currentId) currentId = id + 1;
                    } catch (NumberFormatException e) {
                        // header row, ignore
                    }
                }
            }
        } catch (IOException e) {
            // file doesn't exist yet - fine, it gets created on first add
        }
    }

    public void addProduct(Product p) throws InventoryException {
        validate(p);
        File file = new File(FILE_NAME);
        boolean needsHeader = !file.exists();

        int assignedId;
        synchronized (idLock) {
            assignedId = currentId++;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            if (needsHeader) {
                bw.write("id,name,category,quantity,price,reorder_level\n");
            }
            String record = String.format("%d,%s,%s,%d,%.2f,%d\n",
                    assignedId, p.getName(), p.getCategory(),
                    p.getQuantity(), p.getPrice(), p.getReorderLevel());
            bw.write(record);
        } catch (IOException e) {
            throw new InventoryException(ErrorType.DATABASE_ERROR, "Failed to write product: " + e.getMessage(), e);
        }
    }

    public List<Product> getAllProducts() throws InventoryException {
        List<Product> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String category = parts[2];
                    int qty = Integer.parseInt(parts[3]);
                    double price = Double.parseDouble(parts[4]);
                    int reorder = Integer.parseInt(parts[5]);

                    Product prod = new Product(name, category, qty, price, reorder);
                    prod.setId(id);
                    list.add(prod);
                }
            }
        } catch (FileNotFoundException e) {
            // no products yet - return the empty list
        } catch (IOException e) {
            throw new InventoryException(ErrorType.DATABASE_ERROR, "Failed to read products: " + e.getMessage(), e);
        }
        return list;
    }

    public Product getProductById(int productId) throws InventoryException {
        for (Product p : getAllProducts()) {
            if (p.getId() == productId) {
                return p;
            }
        }
        throw new InventoryException(ErrorType.PRODUCT_NOT_FOUND, "No product with id " + productId);
    }

    public void updateProduct(Product updatedProduct) throws InventoryException {
        validate(updatedProduct);
        List<Product> all = getAllProducts();
        boolean found = false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            bw.write("id,name,category,quantity,price,reorder_level\n");
            for (Product p : all) {
                if (p.getId() == updatedProduct.getId()) {
                    p = updatedProduct;
                    found = true;
                }
                String record = String.format("%d,%s,%s,%d,%.2f,%d\n",
                        p.getId(), p.getName(), p.getCategory(),
                        p.getQuantity(), p.getPrice(), p.getReorderLevel());
                bw.write(record);
            }
        } catch (IOException e) {
            throw new InventoryException(ErrorType.DATABASE_ERROR, "Failed to update product: " + e.getMessage(), e);
        }

        if (!found) {
            throw new InventoryException(ErrorType.PRODUCT_NOT_FOUND, "No product with id " + updatedProduct.getId());
        }
    }

    public void deleteProduct(int id) throws InventoryException {
        List<Product> all = getAllProducts();
        boolean found = all.removeIf(p -> p.getId() == id);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            bw.write("id,name,category,quantity,price,reorder_level\n");
            for (Product p : all) {
                String record = String.format("%d,%s,%s,%d,%.2f,%d\n",
                        p.getId(), p.getName(), p.getCategory(),
                        p.getQuantity(), p.getPrice(), p.getReorderLevel());
                bw.write(record);
            }
        } catch (IOException e) {
            throw new InventoryException(ErrorType.DATABASE_ERROR, "Failed to delete product: " + e.getMessage(), e);
        }

        if (!found) {
            throw new InventoryException(ErrorType.PRODUCT_NOT_FOUND, "No product with id " + id);
        }
    }

    private void validate(Product p) throws InventoryException {
        if (p.getName() == null || p.getName().trim().isEmpty())
            throw new InventoryException(ErrorType.INVALID_PRODUCT, "Name cannot be empty");
        if (p.getQuantity() < 0 || p.getPrice() < 0)
            throw new InventoryException(ErrorType.INVALID_PRODUCT, "Quantity/price cannot be negative");
    }
}


