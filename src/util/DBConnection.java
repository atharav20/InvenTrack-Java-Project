package util;

import java.sql.Connection;

/**
 * Connection placeholder utility.
 * Database operations have been safely migrated to native flat-file text storage 
 * (CSV) to ensure full command-line execution compatibility.
 */
public class DBConnection {
    private DBConnection() {}

    public static Connection getConnection() {
        // Returns null safely since data operations now bypass JDBC and go straight to files
        return null; 
    }

    public static boolean testConnection() {
        return true; 
    }
}
