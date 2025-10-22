import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/payroll_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "214042"; // Change this to your MySQL password
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    private static DatabaseConnection instance;
    private Connection connection;

    // Private constructor for singleton pattern
    private DatabaseConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException ex) {
            System.out.println("Database Connection Creation Failed : " + ex.getMessage());
            throw new SQLException("MySQL JDBC Driver not found", ex);
        }
    }

    // Get singleton instance
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Get connection
    public Connection getConnection() {
        return connection;
    }

    // Test connection
    public static boolean testConnection() {
        try {
            DatabaseConnection dbConnection = getInstance();
            Connection conn = dbConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection successful!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return false;
    }

    // Close connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getMessage());
        }
    }

    // Create connection with custom parameters
    public static Connection createConnection(String url, String user, String pass) throws SQLException {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL JDBC Driver not found", ex);
        }
    }
}