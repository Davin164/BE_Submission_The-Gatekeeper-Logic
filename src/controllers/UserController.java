package controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.UserService;
import models.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserController implements HttpHandler {
    private UserService userService;
    
    public UserController() {
        this.userService = new UserService();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Enable CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        try {
            if (method.equals("GET")) {
                handleGet(exchange, path);
            } else if (method.equals("POST")) {
                handlePost(exchange, path);
            } else if (method.equals("PUT")) {
                handlePut(exchange, path);
            } else if (method.equals("DELETE")) {
                handleDelete(exchange, path);
            } else {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    private void handleGet(HttpExchange exchange, String path) throws Exception {
        if (path.equals("/api/users")) {
            // GET all users
            List<User> users = userService.getAllUsers();
            String json = convertUsersToJson(users);
            sendResponse(exchange, 200, json);
        } 
        else if (path.matches("/api/users/\\d+")) {
            // GET user by ID
            int userId = extractIdFromPath(path);
            User user = userService.getUserById(userId);
            
            if (user != null) {
                String json = convertUserToJson(user);
                sendResponse(exchange, 200, json);
            } else {
                sendResponse(exchange, 404, "{\"error\": \"User not found\"}");
            }
        }
        else {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }
    
    private void handlePost(HttpExchange exchange, String path) throws Exception {
        if (path.equals("/api/users")) {
            String body = readRequestBody(exchange);
            
            String username = extractStringFromJson(body, "username");
            String email = extractStringFromJson(body, "email");
            String password = extractStringFromJson(body, "password");
            String fullName = extractStringFromJson(body, "fullName");
            String phone = extractStringFromJson(body, "phone");
            String role = extractStringFromJson(body, "role");
            
            User user = userService.createUser(username, email, password, fullName, phone, role);
            
            String json = convertUserToJson(user);
            sendResponse(exchange, 201, json);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }
    
    private void handlePut(HttpExchange exchange, String path) throws Exception {
        if (path.matches("/api/users/\\d+")) {
            int userId = extractIdFromPath(path);
            String body = readRequestBody(exchange);
            
            User user = new User();
            user.setUserId(userId);
            
            // Optional fields
            if (body.contains("username")) {
                user.setUsername(extractStringFromJson(body, "username"));
            }
            if (body.contains("email")) {
                user.setEmail(extractStringFromJson(body, "email"));
            }
            if (body.contains("fullName")) {
                user.setFullName(extractStringFromJson(body, "fullName"));
            }
            if (body.contains("phone")) {
                user.setPhone(extractStringFromJson(body, "phone"));
            }
            if (body.contains("password")) {
                user.setPassword(extractStringFromJson(body, "password"));
            }
            
            boolean updated = userService.updateUser(user);
            
            if (updated) {
                sendResponse(exchange, 200, "{\"message\": \"User updated successfully\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\": \"User not found\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws Exception {
        if (path.matches("/api/users/\\d+")) {
            int userId = extractIdFromPath(path);
            boolean deleted = userService.deleteUser(userId);
            
            if (deleted) {
                sendResponse(exchange, 200, "{\"message\": \"User deleted successfully\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\": \"User not found\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }
    
    private int extractIdFromPath(String path) {
        return Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
    }
    
    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }
    
    private String extractStringFromJson(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        throw new IllegalArgumentException("Missing field: " + key);
    }
    
    private String convertUserToJson(User u) {
        return String.format(
            "{\"userId\":%d,\"username\":\"%s\",\"email\":\"%s\",\"fullName\":\"%s\",\"phone\":\"%s\",\"role\":\"%s\",\"createdAt\":\"%s\"}",
            u.getUserId(), u.getUsername(), u.getEmail(), u.getFullName(),
            u.getPhone() != null ? u.getPhone() : "", u.getRole(),
            u.getCreatedAt()
        );
    }
    
    private String convertUsersToJson(List<User> users) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < users.size(); i++) {
            json.append(convertUserToJson(users.get(i)));
            if (i < users.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}