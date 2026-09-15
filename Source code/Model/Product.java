package model;

/**
 * Represents a single product in the inventory.
 * Plain model class used across all three service modules.
 */
public class Product {

    private int id;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private int reorderLevel;

    public Product() {
    }
    /** Full constructor, used when loading an existing product from the database. */
    public Product(int id, String name, String category, int quantity, double price, int reorderLevel) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.reorderLevel = reorderLevel;
    }

