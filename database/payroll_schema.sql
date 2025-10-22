-- Employment Payroll System Database Schema
-- Create database
CREATE DATABASE IF NOT EXISTS payroll_system;
USE payroll_system;

-- Admin table
CREATE TABLE admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Employee table
CREATE TABLE employee (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    basic_salary DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Payroll table
CREATE TABLE payroll (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    bonuses DECIMAL(10, 2) DEFAULT 0.00,
    deductions DECIMAL(10, 2) DEFAULT 0.00,
    net_salary DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
);

-- Insert default admin user
INSERT INTO admin (username, password) VALUES 
('admin', 'admin123');

-- Insert sample employees
INSERT INTO employee (name, department, basic_salary) VALUES 
('John Smith', 'Engineering', 75000.00),
('Jane Doe', 'Marketing', 65000.00),
('Mike Johnson', 'HR', 55000.00),
('Sarah Wilson', 'Finance', 70000.00);

-- Insert sample payroll records
INSERT INTO payroll (employee_id, bonuses, deductions, net_salary) VALUES 
(1, 5000.00, 7500.00, 72500.00),
(2, 3000.00, 6500.00, 61500.00),
(3, 2000.00, 5500.00, 51500.00),
(4, 4000.00, 7000.00, 67000.00);