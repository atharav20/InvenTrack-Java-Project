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
