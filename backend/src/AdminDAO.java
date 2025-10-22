import java.sql.*;

public class AdminDAO {
    private Connection connection;

    public AdminDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Authenticate admin
    public boolean authenticateAdmin(String username, String password) {
        String sql = "SELECT id FROM admin WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if admin exists
            }
        } catch (SQLException e) {
            System.out.println("Error authenticating admin: " + e.getMessage());
            return false;
        }
    }

    // Get admin by username
    public Admin getAdminByUsername(String username) {
        String sql = "SELECT * FROM admin WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setId(rs.getInt("id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setPassword(rs.getString("password"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving admin by username: " + e.getMessage());
        }
        return null;
    }

    // Get admin by ID
    public Admin getAdminById(int id) {
        String sql = "SELECT * FROM admin WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setId(rs.getInt("id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setPassword(rs.getString("password"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving admin by ID: " + e.getMessage());
        }
        return null;
    }

    // Add new admin (for system administration)
    public boolean addAdmin(Admin admin) {
        String sql = "INSERT INTO admin (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, admin.getUsername());
            pstmt.setString(2, admin.getPassword());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error adding admin: " + e.getMessage());
            return false;
        }
    }

    // Update admin password
    public boolean updateAdminPassword(String username, String newPassword) {
        String sql = "UPDATE admin SET password = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error updating admin password: " + e.getMessage());
            return false;
        }
    }

    // Check if username exists
    public boolean usernameExists(String username) {
        String sql = "SELECT id FROM admin WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking username existence: " + e.getMessage());
            return false;
        }
    }

    // Delete admin (for system administration)
    public boolean deleteAdmin(int id) {
        String sql = "DELETE FROM admin WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting admin: " + e.getMessage());
            return false;
        }
    }
}