package service;

import model.Product;
import util.DBConnection;
import util.InventoryException;
import util.InventoryException.ErrorType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * Module 1: Product Management (CRUD).
 * Handles all create, read, update, and delete operations on products
 * via JDBC PreparedStatements.
 */
public class ProductService {

    /** Inserts a new product after validating its fields. */
    public void addProduct(Product p) throws InventoryException {
        validate(p);
        String sql = "INSERT INTO Products (name, category, quantity, price, reorder_level) VALUES (?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, p.getName());
            s.setString(2, p.getCategory());
            s.setInt(3, p.getQuantity());
            s.setDouble(4, p.getPrice());
            s.setInt(5, p.getReorderLevel());
            s.executeUpdate();
        } catch (SQLException e) {
            throw new InventoryException(ErrorType.DATABASE_ERROR, e.getMessage(), e);
        }
    }
    /** Returns every product currently in the database. */
    public List<Product> getAllProducts() throws InventoryException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products";
        try (Connection c = DBConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new InventoryException(ErrorType.DATABASE_ERROR, e.getMessage(), e);
        }
        return list;
    }
/** Looks up a single product by id; throws PRODUCT_NOT_FOUND if it doesn't exist. */
    public Product getProductById(int id) throws InventoryException {
        String sql = "SELECT * FROM Products WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) return map(rs);
                throw new InventoryException(ErrorType.PRODUCT_NOT_FOUND, "No product with id " + id);
            }
        } catch (SQLException e) {
            throw new InventoryException(ErrorType.DATABASE_ERROR, e.getMessage(), e);
        }
    }

    /** Overwrites an existing product's fields with the values in the given Product object. */
    public void updateProduct(Product p) throws InventoryException {
        validate(p);
        String sql = "UPDATE Products SET name=?, category=?, quantity=?, price=?, reorder_level=? WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, p.getName());
            s.setString(2, p.getCategory());
            s.setInt(3, p.getQuantity());
            s.setDouble(4, p.getPrice());
            s.setInt(5, p.getReorderLevel());
            s.setInt(6, p.getId());
            if (s.executeUpdate() == 0)
                throw new InventoryException(ErrorType.PRODUCT_NOT_FOUND, "No product with id " + p.getId());
        } catch (SQLException e) {
            throw new InventoryException(ErrorType.DATABASE_ERROR, e.getMessage(), e);
        }
    }

    /** Removes a product permanently by id. */
    public void deleteProduct(int id) throws InventoryException {
        String sql = "DELETE FROM Products WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id);
            if (s.executeUpdate() == 0)
                throw new InventoryException(ErrorType.PRODUCT_NOT_FOUND, "No product with id " + id);
        } catch (SQLException e) {
            throw new InventoryException(ErrorType.DATABASE_ERROR, e.getMessage(), e);
        }
    }

    /** Basic field validation shared by add and update. */
    private void validate(Product p) throws InventoryException {
        if (p.getName() == null || p.getName().trim().isEmpty())
            throw new InventoryException(ErrorType.INVALID_PRODUCT, "Name cannot be empty");
        if (p.getQuantity() < 0 || p.getPrice() < 0)
            throw new InventoryException(ErrorType.INVALID_PRODUCT, "Quantity/price cannot be negative");
    }

    /** Converts one row of a ResultSet into a Product object. */
    private Product map(ResultSet rs) throws SQLException {
        return new Product(rs.getInt("id"), rs.getString("name"), rs.getString("category"),
                rs.getInt("quantity"), rs.getDouble("price"), rs.getInt("reorder_level"));
    }
}
