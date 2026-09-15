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

