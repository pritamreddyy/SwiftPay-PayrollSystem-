import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {
    private Connection connection;

    public PayrollDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Create payroll record
    public boolean addPayroll(Payroll payroll) {
        String sql = "INSERT INTO payroll (employee_id, bonuses, deductions, net_salary) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, payroll.getEmployeeId());
            pstmt.setBigDecimal(2, payroll.getBonuses());
            pstmt.setBigDecimal(3, payroll.getDeductions());
            pstmt.setBigDecimal(4, payroll.getNetSalary());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error adding payroll: " + e.getMessage());
            return false;
        }
    }

    // Get all payroll records with employee details
    public List<Payroll> getAllPayrollRecords() {
        List<Payroll> payrolls = new ArrayList<>();
        String sql = """
            SELECT p.*, e.name as employee_name, e.basic_salary
            FROM payroll p
            JOIN employee e ON p.employee_id = e.id
            ORDER BY p.id DESC
            """;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Payroll payroll = new Payroll();
                payroll.setId(rs.getInt("id"));
                payroll.setEmployeeId(rs.getInt("employee_id"));
                payroll.setBonuses(rs.getBigDecimal("bonuses"));
                payroll.setDeductions(rs.getBigDecimal("deductions"));
                payroll.setNetSalary(rs.getBigDecimal("net_salary"));
                payroll.setCreatedAt(rs.getTimestamp("created_at"));
                payrolls.add(payroll);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving payroll records: " + e.getMessage());
        }
        return payrolls;
    }

    // Get payroll record by ID
    public Payroll getPayrollById(int id) {
        String sql = "SELECT * FROM payroll WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Payroll payroll = new Payroll();
                    payroll.setId(rs.getInt("id"));
                    payroll.setEmployeeId(rs.getInt("employee_id"));
                    payroll.setBonuses(rs.getBigDecimal("bonuses"));
                    payroll.setDeductions(rs.getBigDecimal("deductions"));
                    payroll.setNetSalary(rs.getBigDecimal("net_salary"));
                    payroll.setCreatedAt(rs.getTimestamp("created_at"));
                    return payroll;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving payroll by ID: " + e.getMessage());
        }
        return null;
    }

    // Get payroll records by employee ID
    public List<Payroll> getPayrollByEmployeeId(int employeeId) {
        List<Payroll> payrolls = new ArrayList<>();
        String sql = "SELECT * FROM payroll WHERE employee_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Payroll payroll = new Payroll();
                    payroll.setId(rs.getInt("id"));
                    payroll.setEmployeeId(rs.getInt("employee_id"));
                    payroll.setBonuses(rs.getBigDecimal("bonuses"));
                    payroll.setDeductions(rs.getBigDecimal("deductions"));
                    payroll.setNetSalary(rs.getBigDecimal("net_salary"));
                    payroll.setCreatedAt(rs.getTimestamp("created_at"));
                    payrolls.add(payroll);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving payroll by employee ID: " + e.getMessage());
        }
        return payrolls;
    }

    // Update payroll record
    public boolean updatePayroll(Payroll payroll) {
        String sql = "UPDATE payroll SET bonuses = ?, deductions = ?, net_salary = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, payroll.getBonuses());
            pstmt.setBigDecimal(2, payroll.getDeductions());
            pstmt.setBigDecimal(3, payroll.getNetSalary());
            pstmt.setInt(4, payroll.getId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error updating payroll: " + e.getMessage());
            return false;
        }
    }

    // Delete payroll record
    public boolean deletePayroll(int id) {
        String sql = "DELETE FROM payroll WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting payroll: " + e.getMessage());
            return false;
        }
    }

    // Calculate and create payroll for employee
    public boolean calculatePayroll(int employeeId, BigDecimal bonuses, BigDecimal deductions) {
        // First get employee's basic salary
        String getEmployeeSql = "SELECT basic_salary FROM employee WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(getEmployeeSql)) {
            pstmt.setInt(1, employeeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal basicSalary = rs.getBigDecimal("basic_salary");
                    BigDecimal netSalary = basicSalary.add(bonuses).subtract(deductions);
                    
                    // Create new payroll record
                    Payroll payroll = new Payroll(employeeId, bonuses, deductions, netSalary);
                    return addPayroll(payroll);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error calculating payroll: " + e.getMessage());
        }
        return false;
    }

    // Get payroll summary statistics
    public String getPayrollSummary() {
        String sql = """
            SELECT 
                COUNT(*) as total_records,
                SUM(net_salary) as total_payout,
                AVG(net_salary) as average_salary
            FROM payroll
            """;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return String.format(
                    "Total Records: %d, Total Payout: $%.2f, Average Salary: $%.2f",
                    rs.getInt("total_records"),
                    rs.getBigDecimal("total_payout"),
                    rs.getBigDecimal("average_salary")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error getting payroll summary: " + e.getMessage());
        }
        return "No payroll data available";
    }
}