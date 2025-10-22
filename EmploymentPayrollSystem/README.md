# Employment Payroll System

A complete, ready-to-run Employment Payroll Management System built using pure Java (JDBC + MySQL) for backend, and HTML, CSS, JavaScript for frontend. No frameworks or libraries used - just core technologies.

## 🚀 Features

- **Admin Authentication**: Secure login system for administrators
- **Employee Management**: Complete CRUD operations (Create, Read, Update, Delete)
- **Payroll Processing**: Calculate salaries with bonuses, deductions, and net pay
- **Real-time Updates**: Live dashboard with employee and payroll statistics
- **Responsive UI**: Modern, mobile-friendly interface
- **Database Integration**: Full MySQL integration with sample data

## 📋 Prerequisites

Before running this application, ensure you have the following installed:

1. **Java Development Kit (JDK) 8 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use OpenJDK
   - Verify installation: `java -version`

2. **MySQL Server 5.7 or higher**
   - Download from [MySQL Official Site](https://dev.mysql.com/downloads/)
   - Ensure MySQL service is running
   - Default port: 3306

3. **Visual Studio Code (Recommended)**
   - Download from [VS Code Official Site](https://code.visualstudio.com/)
   - Install Java Extension Pack

4. **Modern Web Browser**
   - Chrome, Firefox, Safari, or Edge (latest versions)

## 📁 Project Structure

```
EmploymentPayrollSystem/
├── backend/
│   └── src/
│       ├── Admin.java               # Admin model class
│       ├── Employee.java            # Employee model class
│       ├── Payroll.java            # Payroll model class
│       ├── AdminDAO.java           # Admin data access object
│       ├── EmployeeDAO.java        # Employee data access object
│       ├── PayrollDAO.java         # Payroll data access object
│       ├── DatabaseConnection.java  # MySQL connection manager
│       └── MainServer.java         # HTTP server (com.sun.net.httpserver)
├── frontend/
│   ├── index.html                  # Login page
│   ├── dashboard.html              # Main dashboard
│   ├── employees.html              # Employee management
│   ├── payroll.html               # Payroll management
│   ├── style.css                  # Styling for all pages
│   └── script.js                  # Shared JavaScript functions
├── database/
│   └── payroll_schema.sql         # Database schema and sample data
├── .vscode/
│   ├── launch.json                # VS Code launch configurations
│   └── tasks.json                 # VS Code build tasks
└── README.md                      # This file
```

## 🛠️ Installation & Setup

### Step 1: Clone or Download the Project

```bash
# If you have git installed
git clone <repository-url>
cd EmploymentPayrollSystem

# Or download and extract the ZIP file
```

### Step 2: Database Setup

1. **Start MySQL Server**
   ```bash
   # On Windows (if MySQL is in PATH)
   net start mysql

   # On macOS/Linux
   sudo service mysql start
   # or
   sudo systemctl start mysql
   ```

2. **Create Database and Tables**
   ```bash
   # Login to MySQL
   mysql -u root -p

   # Run the schema file
   source database/payroll_schema.sql

   # Or alternatively, copy-paste the contents of payroll_schema.sql
   ```

3. **Verify Database Setup**
   ```sql
   USE payroll_system;
   SHOW TABLES;
   SELECT * FROM admin;
   SELECT * FROM employee;
   ```

### Step 3: Configure Database Connection

1. **Edit Database Connection Settings** (if needed)
   - Open `backend/src/DatabaseConnection.java`
   - Update the following constants if your MySQL setup differs:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/payroll_system";
   private static final String USERNAME = "root";
   private static final String PASSWORD = ""; // Change to your MySQL password
   ```

### Step 4: VS Code Setup (Recommended Method)

1. **Open Project in VS Code**
   ```bash
   code .
   ```

2. **Install Required Extensions**
   - Java Extension Pack (includes Language Support for Java)
   - Ensure the extensions are enabled

3. **One-Click Run**
   - Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac)
   - Type "Tasks: Run Task" and select it
   - Choose "run-server" from the list
   - The system will automatically:
     - Create necessary directories
     - Download MySQL JDBC driver
     - Compile all Java files
     - Start the server

4. **Alternative: Using Run Configuration**
   - Go to Run and Debug view (`Ctrl+Shift+D`)
   - Select "Start Payroll Server" from the dropdown
   - Click the green play button or press `F5`

### Step 5: Manual Setup (Alternative Method)

If you prefer command-line setup:

1. **Create Required Directories**
   ```bash
   mkdir -p lib backend/bin
   ```

2. **Download MySQL JDBC Driver**
   ```bash
   # Download MySQL Connector/J
   curl -L -o lib/mysql-connector-java.jar https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar
   ```

3. **Compile Java Files**
   ```bash
   # Compile all Java source files
   javac -cp lib/* -d backend/bin backend/src/*.java
   ```

4. **Run the Server**
   ```bash
   # Start the application
   java -cp "backend/bin:lib/*" MainServer

   # On Windows, use semicolon instead of colon
   java -cp "backend/bin;lib/*" MainServer
   ```

## 🚀 Running the Application

### Starting the Server

1. **Using VS Code** (Recommended):
   - Open the project in VS Code
   - Go to Run and Debug (`Ctrl+Shift+D`)
   - Select "Start Payroll Server"
   - Press `F5` or click the green play button

2. **Using Command Line**:
   ```bash
   java -cp "backend/bin:lib/*" MainServer
   ```

3. **Server Status**:
   You should see output similar to:
   ```
   Testing database connection...
   Database connected successfully!
   Employment Payroll Server started on port 8080
   Frontend URL: http://localhost:8080/frontend/index.html
   API Base URL: http://localhost:8080/api/
   ```

### Accessing the Application

1. **Open your web browser**
2. **Navigate to**: `http://localhost:8080/frontend/index.html`
3. **Login with default credentials**:
   - **Username**: `admin`
   - **Password**: `admin123`

## 💻 Usage Guide

### Admin Dashboard
- View system statistics (total employees, payroll records, average salary)
- Navigate to employee and payroll management sections
- Real-time data updates

### Employee Management
- **Add Employees**: Click "Add New Employee" and fill in the details
- **Edit Employees**: Click "Edit" button next to any employee
- **Delete Employees**: Click "Delete" and confirm the action
- **Search**: Use the search bar to find employees by name or department

### Payroll Management
- **Calculate Payroll**: Click "Calculate New Payroll"
- **Select Employee**: Choose from dropdown list
- **Enter Values**: Add bonuses and deductions
- **Preview**: See real-time calculation preview
- **Save**: Click "Calculate & Save" to create the payroll record

### Sample Data Included
The system comes with:
- 4 sample employees from different departments
- 4 sample payroll records
- 1 admin user for login

## 🔧 Configuration

### Port Configuration
To change the server port, edit `MainServer.java`:
```java
private static final int PORT = 8080; // Change this value
```

### Database Configuration
Update `DatabaseConnection.java` for different database settings:
```java
private static final String URL = "jdbc:mysql://localhost:3306/payroll_system";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

## 🛠️ Development

### Adding New Features
1. **Backend**: Add new endpoints in `MainServer.java`
2. **Frontend**: Create new HTML files and update navigation
3. **Database**: Modify `payroll_schema.sql` and update DAO classes

### VS Code Tasks Available
- `compile-java`: Compile all Java source files
- `run-server`: Compile and run the server
- `clean-build`: Clean and rebuild the project
- `setup-database`: Run the database schema
- `open-frontend`: Open the frontend in browser

### Debugging
- Use "Debug Payroll Server" configuration in VS Code
- Set breakpoints in Java files
- Debug port: 5005

## 📊 API Endpoints

### Authentication
- `POST /api/login` - Admin login

### Employee Management
- `GET /api/employees` - Get all employees
- `POST /api/employees` - Create new employee
- `GET /api/employee/{id}` - Get employee by ID
- `PUT /api/employee/{id}` - Update employee
- `DELETE /api/employee/{id}` - Delete employee

### Payroll Management
- `GET /api/payroll` - Get all payroll records
- `POST /api/payroll/calculate` - Calculate and save payroll

## 🔒 Security Features

- Admin authentication required for all operations
- Session-based security using localStorage
- Input validation on both frontend and backend
- SQL injection prevention using PreparedStatements
- CORS headers configured for cross-origin requests

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Ensure MySQL server is running
   - Check username/password in `DatabaseConnection.java`
   - Verify database `payroll_system` exists

2. **Port Already in Use**
   - Change port in `MainServer.java`
   - Kill process using port 8080: `netstat -ano | findstr :8080`

3. **Compilation Errors**
   - Ensure JDK is properly installed
   - Check JAVA_HOME environment variable
   - Verify MySQL JDBC driver is downloaded

4. **Frontend Not Loading**
   - Ensure server is running on port 8080
   - Check browser console for errors
   - Try different browser or incognito mode

5. **VS Code Issues**
   - Reload window: `Ctrl+Shift+P` → "Developer: Reload Window"
   - Check Java Extension Pack is installed and enabled
   - Verify Java path in VS Code settings

### Error Messages

- **"MySQL JDBC Driver not found"**: Download the JDBC driver to `lib/` folder
- **"Database connection failed"**: Check MySQL service and credentials
- **"Port 8080 is already in use"**: Change port or kill the process using it

## 📝 License

This project is created for educational purposes. Feel free to use, modify, and distribute as needed.

## 🤝 Contributing

This is a complete, standalone project. If you'd like to add features:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📧 Support

For issues or questions:
1. Check the troubleshooting section above
2. Verify your setup matches the prerequisites
3. Ensure all steps in the installation guide were followed

---

**Note**: This system uses only pure Java, HTML, CSS, and JavaScript - no frameworks or external libraries except the MySQL JDBC driver. It's designed to demonstrate core web development concepts without additional complexity.