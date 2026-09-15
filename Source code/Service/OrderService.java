package service;
import model.Product;
import util.DBConnection;
import util.InventoryException;
import util.InventoryException.ErrorType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

