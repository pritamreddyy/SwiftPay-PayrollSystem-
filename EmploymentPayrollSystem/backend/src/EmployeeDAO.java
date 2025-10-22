import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private Connection connection;

    public EmployeeDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Create employee
    public boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO employee (name, department, basic_salary) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getDepartment());
            pstmt.setBigDecimal(3, employee.getBasicSalary());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error adding employee: " + e.getMessage());
            return false;
        }
    }

    // Get all employees
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee ORDER BY id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setName(rs.getString("name"));
                employee.setDepartment(rs.getString("department"));
                employee.setBasicSalary(rs.getBigDecimal("basic_salary"));
                employee.setCreatedAt(rs.getTimestamp("created_at"));
                employee.setUpdatedAt(rs.getTimestamp("updated_at"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employees: " + e.getMessage());
        }
        return employees;
    }

    // Get employee by ID
    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employee WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId(rs.getInt("id"));
                    employee.setName(rs.getString("name"));
                    employee.setDepartment(rs.getString("department"));
                    employee.setBasicSalary(rs.getBigDecimal("basic_salary"));
                    employee.setCreatedAt(rs.getTimestamp("created_at"));
                    employee.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return employee;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving employee by ID: " + e.getMessage());
        }
        return null;
    }

    // Update employee
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employee SET name = ?, department = ?, basic_salary = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getDepartment());
            pstmt.setBigDecimal(3, employee.getBasicSalary());
            pstmt.setInt(4, employee.getId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }

    // Delete employee
    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM employee WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    // Search employees by name or department
    public List<Employee> searchEmployees(String searchTerm) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee WHERE name LIKE ? OR department LIKE ? ORDER BY name";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId(rs.getInt("id"));
                    employee.setName(rs.getString("name"));
                    employee.setDepartment(rs.getString("department"));
                    employee.setBasicSalary(rs.getBigDecimal("basic_salary"));
                    employee.setCreatedAt(rs.getTimestamp("created_at"));
                    employee.setUpdatedAt(rs.getTimestamp("updated_at"));
                    employees.add(employee);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching employees: " + e.getMessage());
        }
        return employees;
    }
}