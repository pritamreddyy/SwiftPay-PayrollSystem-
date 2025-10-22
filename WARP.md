# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Common Development Commands

### Project Setup
```powershell
# Create required directories
New-Item -ItemType Directory -Force -Path "lib", "backend\bin"

# Download MySQL JDBC driver (Windows PowerShell)
Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar" -OutFile "lib\mysql-connector-java.jar"
```

### Build Commands
```powershell
# Clean build
if (Test-Path "backend\bin") { Remove-Item -Recurse -Force "backend\bin" }
New-Item -ItemType Directory -Force -Path "backend\bin"

# Compile Java source files
javac -cp "lib\*" -d "backend\bin" backend\src\*.java

# Alternative: Use VS Code task
# Ctrl+Shift+P → "Tasks: Run Task" → "compile-java"
```

### Running the Application
```powershell
# Run the server (Windows)
java -cp "backend\bin;lib\*" MainServer

# Alternative: Use VS Code launch configuration
# F5 or "Start Payroll Server" configuration
```

### Database Setup
```powershell
# Setup database (requires MySQL installed)
mysql -u root -p < database\payroll_schema.sql

# Alternative: Use VS Code task
# Ctrl+Shift+P → "Tasks: Run Task" → "setup-database"
```

### Development Tasks
```powershell
# Full setup and run
# VS Code task: "full-setup"

# Open frontend in browser
# VS Code task: "open-frontend" or navigate to:
# http://localhost:8080/frontend/index.html

# Test database connection
# Use VS Code launch config: "Test Database Connection"
```

### Single Test/Component Development
```java
// To test individual DAO classes, create a main method:
public static void main(String[] args) {
    try {
        EmployeeDAO dao = new EmployeeDAO();
        // Test specific functionality
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

## High-Level Architecture

### System Architecture
This is a **3-tier web application** with clear separation of concerns:

1. **Presentation Layer**: Static HTML/CSS/JS files served by the Java HTTP server
2. **Business Logic Layer**: Java classes handling HTTP requests and business rules
3. **Data Access Layer**: DAO pattern with direct MySQL connectivity

### Core Components

#### HTTP Server Architecture (`MainServer.java`)
- Uses `com.sun.net.httpserver.HttpServer` (built-in Java HTTP server)
- **RESTful API Design**: Different handlers for different endpoints
- **Handler Pattern**: Each major feature has its own handler class
- **CORS Support**: Built-in CORS headers for frontend-backend communication
- **Static File Serving**: Serves frontend files directly from filesystem

#### Data Access Pattern
- **DAO Pattern**: Clear separation between data models and database operations
- **Singleton Database Connection**: `DatabaseConnection` class manages single connection instance
- **PreparedStatements**: All SQL uses prepared statements to prevent injection
- **Model Classes**: `Employee`, `Payroll`, `Admin` as POJOs with proper encapsulation

#### API Endpoint Structure
```
/api/login                    - POST: Admin authentication
/api/employees               - GET: List all | POST: Create new
/api/employee/{id}           - GET: By ID | PUT: Update | DELETE: Remove
/api/payroll                 - GET: All payroll records
/api/payroll/calculate       - POST: Calculate and save new payroll
/frontend/*                  - Static file serving for web UI
```

### Key Design Patterns

#### Singleton Pattern
- `DatabaseConnection` ensures single connection instance
- Thread-safe with lazy initialization and connection validation

#### DAO Pattern
- `EmployeeDAO`, `PayrollDAO`, `AdminDAO` encapsulate all database operations
- Clean separation between business logic and data persistence
- Consistent error handling and logging

#### Handler Pattern
- Each API endpoint group has dedicated handler class
- Shared utility methods for common operations (CORS, error responses, JSON conversion)
- Modular structure allows easy addition of new endpoints

### Database Schema Design
- **Referential Integrity**: Payroll table has foreign key to employee table
- **Cascade Delete**: Removing employee removes associated payroll records
- **Timestamps**: Automatic created/updated tracking on employee records
- **Decimal Precision**: Financial data uses `DECIMAL(10,2)` for accuracy

### Frontend-Backend Communication
- **Form-based API**: Uses URL-encoded form data, not JSON
- **Session Management**: Client-side session storage for authentication state
- **Real-time Updates**: Frontend polls API for dashboard statistics
- **Error Handling**: Consistent JSON error responses with success/failure indicators

## Development Context

### Technology Stack
- **Backend**: Pure Java 8+ with JDBC, no frameworks
- **Frontend**: Vanilla HTML5, CSS3, JavaScript (ES6+)
- **Database**: MySQL 5.7+
- **HTTP Server**: Java built-in `com.sun.net.httpserver`
- **Build System**: Manual compilation via javac (no Maven/Gradle)

### Port Configuration
- **Default Port**: 8080
- **Change in**: `MainServer.java` line 14: `private static final int PORT = 8080;`

### Database Configuration
- **Connection**: Modify `DatabaseConnection.java` lines 6-8
- **Default**: `localhost:3306/payroll_system`, user: `root`, password: empty

### VS Code Integration
- **Pre-configured Tasks**: Comprehensive build, run, and setup tasks
- **Launch Configurations**: Run, debug, and database test configurations
- **Cross-platform**: Tasks configured for Windows, macOS, and Linux

### Security Considerations
- **SQL Injection Prevention**: All database queries use PreparedStatements
- **Basic Authentication**: Simple username/password check (not production-ready)
- **CORS Headers**: Configured for local development (wildcard origins)
- **Input Validation**: Basic validation on both frontend and backend

### Debugging and Troubleshooting
- **Database Connection**: Use "Test Database Connection" launch config
- **Debug Mode**: "Debug Payroll Server" config enables remote debugging on port 5005
- **Error Logging**: All database errors logged to console with descriptive messages
- **Network Issues**: Check if MySQL service is running and port 8080 is available

### Extension Points
- **New Endpoints**: Add handler in `MainServer.java` and create context
- **New Entities**: Follow Employee model - create POJO, DAO, and API handlers  
- **Frontend Pages**: Add HTML file to frontend directory, update navigation
- **Database Changes**: Modify schema, update corresponding DAO and model classes