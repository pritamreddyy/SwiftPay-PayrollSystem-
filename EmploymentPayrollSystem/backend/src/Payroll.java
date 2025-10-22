import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payroll {
    private int id;
    private int employeeId;
    private BigDecimal bonuses;
    private BigDecimal deductions;
    private BigDecimal netSalary;
    private Timestamp createdAt;

    // Default constructor
    public Payroll() {}

    // Constructor with parameters
    public Payroll(int employeeId, BigDecimal bonuses, BigDecimal deductions, BigDecimal netSalary) {
        this.employeeId = employeeId;
        this.bonuses = bonuses;
        this.deductions = deductions;
        this.netSalary = netSalary;
    }

    // Constructor with all fields
    public Payroll(int id, int employeeId, BigDecimal bonuses, BigDecimal deductions, 
                  BigDecimal netSalary, Timestamp createdAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.bonuses = bonuses;
        this.deductions = deductions;
        this.netSalary = netSalary;
        this.createdAt = createdAt;
    }

    // Method to calculate net salary
    public void calculateNetSalary(BigDecimal basicSalary) {
        this.netSalary = basicSalary.add(bonuses).subtract(deductions);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public BigDecimal getBonuses() {
        return bonuses;
    }

    public void setBonuses(BigDecimal bonuses) {
        this.bonuses = bonuses;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Payroll{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", bonuses=" + bonuses +
                ", deductions=" + deductions +
                ", netSalary=" + netSalary +
                ", createdAt=" + createdAt +
                '}';
    }
}