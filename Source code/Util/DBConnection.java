package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
/**
 * Single utility class responsible for opening a JDBC connection to the
 * SQLite database. Connection details are read from config.properties.
 * On first use, it also automatically runs sql/schema.sql to create the
 * required tables, so no manual database setup step is needed.
 */
public class DBConnection {
    private static final String CONFIG_FILE = "config.properties";
    private static final String SCHEMA_FILE = "sql/schema.sql";
    private static String dbUrl;
    private static boolean schemaInitialized = false;
    static {
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            Properties props = new Properties();
            props.load(input);
            dbUrl = props.getProperty("db.url", "jdbc:sqlite:inventrack.db");
        } catch (IOException e) {
            dbUrl = "jdbc:sqlite:inventrack.db";
            System.out.println("Warning: config.properties not found, using default DB path.");
        }
    }
    private DBConnection() {
    }
    /** Opens a connection, initializing the schema first if this is the first call. */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(dbUrl);
        if (!schemaInitialized) {
            initializeSchema(conn);
            schemaInitialized = true;
        }
        return conn;
    }
    /**
     * Reads sql/schema.sql and executes each statement. Safe to call every
     * app startup since the schema uses CREATE TABLE IF NOT EXISTS.
     */
    private static void initializeSchema(Connection conn) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(SCHEMA_FILE)));
            // Strip line comments, then split into individual statements on ';'
            String cleaned = content.replaceAll("--.*", "");
            String[] statements = cleaned.split(";");

            try (Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    String trimmed = sql.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read " + SCHEMA_FILE + " - tables must already exist.");
        } catch (SQLException e) {
            System.out.println("Warning: schema initialization issue: " + e.getMessage());
        }
    }
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}
