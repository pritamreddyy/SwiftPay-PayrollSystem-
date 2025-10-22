import java.math.BigDecimal;
import java.sql.Timestamp;

public class Employee {
    private int id;
    private String name;
    private String department;
    private BigDecimal basicSalary;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public Employee() {}

    // Constructor with parameters
    public Employee(String name, String department, BigDecimal basicSalary) {
        this.name = name;
        this.department = department;
        this.basicSalary = basicSalary;
    }

    // Constructor with all fields
    public Employee(int id, String name, String department, BigDecimal basicSalary, 
                   Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.basicSalary = basicSalary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", basicSalary=" + basicSalary +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}