import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

public class MainServer {
    private static final int PORT = 8080;
    private static HttpServer server;

    public static void main(String[] args) {
        try {
            System.out.println("Testing database connection...");
            if (DatabaseConnection.testConnection()) {
                System.out.println("✅ Database connected successfully!");
            } else {
                System.out.println("❌ Database connection failed!");
                return;
            }

            server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Register routes
            server.createContext("/", new CORSHandler());
            server.createContext("/cors", new CORSHandler());
            server.createContext("/api/login", new LoginHandler());
            server.createContext("/api/employees", new EmployeeHandler());
            server.createContext("/api/employee/", new SingleEmployeeHandler());
            server.createContext("/api/payroll", new PayrollHandler());
            server.createContext("/api/payroll/calculate", new PayrollCalculateHandler());
            server.createContext("/frontend", new StaticFileHandler());

            server.setExecutor(null);
            server.start();

            System.out.println("🚀 Employment Payroll Server started on port " + PORT);
            System.out.println("Frontend: http://localhost:" + PORT + "/frontend/index.html");
            System.out.println("API Base: http://localhost:" + PORT + "/api/");
        } catch (IOException e) {
            System.out.println("Failed to start server: " + e.getMessage());
        }
    }

    // ---------------------- CORS HANDLER ----------------------
    static class CORSHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCORSHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            String response = "CORS OK";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    // ---------------------- LOGIN HANDLER ----------------------
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCORSHeaders(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String requestBody = readRequestBody(exchange);
                    Map<String, String> params = parseFormData(requestBody);

                    String username = params.get("username");
                    String password = params.get("password");

                    AdminDAO adminDAO = new AdminDAO();
                    boolean isAuthenticated = adminDAO.authenticateAdmin(username, password);

                    String response = isAuthenticated
                            ? "{\"success\": true, \"message\": \"Login successful\"}"
                            : "{\"success\": false, \"message\": \"Invalid credentials\"}";

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(isAuthenticated ? 200 : 401, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (SQLException e) {
                    sendErrorResponse(exchange, "Database error: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, "Method not allowed");
            }
        }
    }

    // ---------------------- EMPLOYEE HANDLER ----------------------
    static class EmployeeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCORSHeaders(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
                return;
            }

            try {
                EmployeeDAO dao = new EmployeeDAO();
                String method = exchange.getRequestMethod();

                switch (method) {
                    case "GET":
                        List<Employee> employees = dao.getAllEmployees();
                        String json = convertEmployeesToJSON(employees);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, json.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(json.getBytes());
                        }
                        break;

                    case "POST":
                        String body = readRequestBody(exchange);
                        Map<String, String> params = parseFormData(body);
                        String name = params.get("name");
                        String dept = params.get("department");
                        String salaryStr = params.get("basic_salary");

                        if (name == null || dept == null || salaryStr == null) {
                            sendErrorResponse(exchange, "Missing fields");
                            return;
                        }

                        BigDecimal salary = new BigDecimal(salaryStr);
                        Employee emp = new Employee(name, dept, salary);
                        boolean added = dao.addEmployee(emp);
                        String resp = added ? "{\"success\":true}" : "{\"success\":false}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(added ? 200 : 500, resp.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(resp.getBytes());
                        }
                        break;

                    default:
                        sendErrorResponse(exchange, "Method not allowed");
                }
            } catch (SQLException e) {
                sendErrorResponse(exchange, "Database error: " + e.getMessage());
            }
        }
    }

    // ---------------------- SINGLE EMPLOYEE HANDLER ----------------------
    static class SingleEmployeeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCORSHeaders(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
                return;
            }

            try {
                String[] parts = exchange.getRequestURI().getPath().split("/");
                if (parts.length < 4) {
                    sendErrorResponse(exchange, "Invalid employee ID");
                    return;
                }

                int id = Integer.parseInt(parts[3]);
                EmployeeDAO dao = new EmployeeDAO();

                switch (exchange.getRequestMethod()) {
                    case "GET":
                        Employee e = dao.getEmployeeById(id);
                        if (e != null) {
                            String json = convertEmployeeToJSON(e);
                            exchange.getResponseHeaders().set("Content-Type", "application/json");
                            exchange.sendResponseHeaders(200, json.length());
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(json.getBytes());
                            }
                        } else sendErrorResponse(exchange, "Not found");
                        break;

                    case "DELETE":
                        boolean deleted = dao.deleteEmployee(id);
                        String delResp = deleted ? "{\"success\":true}" : "{\"success\":false}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(deleted ? 200 : 500, delResp.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(delResp.getBytes());
                        }
                        break;

                    default:
                        sendErrorResponse(exchange, "Method not allowed");
                }
            } catch (Exception ex) {
                sendErrorResponse(exchange, "Error: " + ex.getMessage());
            }
        }
    }

    // ---------------------- PAYROLL HANDLER ----------------------
    static class PayrollHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCORSHeaders(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
                return;
            }

            try {
                PayrollDAO dao = new PayrollDAO();
                if ("GET".equals(exchange.getRequestMethod())) {
                    List<Payroll> list = dao.getAllPayrollRecords();
                    String json = convertPayrollsToJSON(list);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, json.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(json.getBytes());
                    }
                } else {
                    sendErrorResponse(exchange, "Method not allowed");
                }
            } catch (SQLException e) {
                sendErrorResponse(exchange, "Database error: " + e.getMessage());
            }
        }
    }

    // ---------------------- PAYROLL CALCULATE HANDLER ----------------------
    static class PayrollCalculateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCORSHeaders(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String body = readRequestBody(exchange);
                    Map<String, String> params = parseFormData(body);

                    int empId = Integer.parseInt(params.get("employee_id"));
                    BigDecimal bonus = new BigDecimal(params.get("bonuses"));
                    BigDecimal ded = new BigDecimal(params.get("deductions"));

                    PayrollDAO dao = new PayrollDAO();
                    boolean ok = dao.calculatePayroll(empId, bonus, ded);

                    String response = ok ? "{\"success\":true}" : "{\"success\":false}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(ok ? 200 : 500, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, "Method not allowed");
            }
        }
    }

    // ---------------------- STATIC FILE HANDLER ----------------------
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String fileName = path.substring(path.lastIndexOf("/") + 1);
            if (fileName.isEmpty() || fileName.equals("frontend")) fileName = "index.html";

            File file = new File("frontend/" + fileName);
            if (file.exists()) {
                String contentType = getContentType(fileName);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
                exchange.sendResponseHeaders(200, data.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(data);
                }
            } else sendErrorResponse(exchange, "File not found");
        }
    }

    // ---------------------- UTILITIES ----------------------
    private static void addCORSHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private static Map<String, String> parseFormData(String data) {
        Map<String, String> map = new HashMap<>();
        if (data != null && !data.isEmpty()) {
            for (String pair : data.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    map.put(java.net.URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                            java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                }
            }
        }
        return map;
    }

    private static void sendErrorResponse(HttpExchange exchange, String msg) throws IOException {
        String res = "{\"success\":false,\"message\":\"" + msg + "\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(400, res.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(res.getBytes());
        }
    }

    private static String getContentType(String name) {
        if (name.endsWith(".html")) return "text/html";
        if (name.endsWith(".css")) return "text/css";
        if (name.endsWith(".js")) return "application/javascript";
        return "application/octet-stream";
    }

    private static String convertEmployeesToJSON(List<Employee> employees) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < employees.size(); i++) {
            if (i > 0) json.append(",");
            json.append(convertEmployeeToJSON(employees.get(i)));
        }
        json.append("]");
        return json.toString();
    }

    private static String convertEmployeeToJSON(Employee e) {
        return String.format("{\"id\":%d,\"name\":\"%s\",\"department\":\"%s\",\"basic_salary\":%.2f}",
                e.getId(), e.getName(), e.getDepartment(), e.getBasicSalary());
    }

    private static String convertPayrollsToJSON(List<Payroll> payrolls) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < payrolls.size(); i++) {
            if (i > 0) json.append(",");
            Payroll p = payrolls.get(i);
            json.append(String.format(
                    "{\"id\":%d,\"employee_id\":%d,\"bonuses\":%.2f,\"deductions\":%.2f,\"net_salary\":%.2f}",
                    p.getId(), p.getEmployeeId(), p.getBonuses(), p.getDeductions(), p.getNetSalary()));
        }
        json.append("]");
        return json.toString();
    }
}
