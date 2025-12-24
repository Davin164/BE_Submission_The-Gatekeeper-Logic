package services;

import dao.UserDAO;
import models.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAll();
    }
    
    public User getUserById(int userId) throws SQLException {
        return userDAO.getById(userId);
    }
    
    public User createUser(String username, String email, String password,
                          String fullName, String phone, String role) throws Exception {
        
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (!isValidRole(role)) {
            throw new IllegalArgumentException("Invalid role. Must be ORGANIZER or CUSTOMER");
        }
        
        try {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password); // In production, hash this!
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setRole(role);
            
            return userDAO.create(user);
        } catch (SQLException e) {
            throw new Exception("Failed to create user: " + e.getMessage());
        }
    }
    
    public boolean updateUser(User user) throws SQLException {
        return userDAO.update(user);
    }
    
    public boolean deleteUser(int userId) throws SQLException {
        return userDAO.delete(userId);
    }
    
    private boolean isValidRole(String role) {
        return role.equals("ORGANIZER") || role.equals("CUSTOMER");
    }
}