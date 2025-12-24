package dao;

import config.DatabaseConfig;
import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }
    
    public User getById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        }
    }
    
    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, password, full_name, phone, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getRole());
            
            stmt.executeUpdate();
            
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setUserId(generatedKeys.getInt(1));
            }
            
            return user;
        }
    }
    
    public boolean update(User user) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        List<Object> params = new ArrayList<>();
        boolean first = true;
        
        if (user.getUsername() != null) {
            if (!first) sql.append(", ");
            sql.append("username = ?");
            params.add(user.getUsername());
            first = false;
        }
        
        if (user.getEmail() != null) {
            if (!first) sql.append(", ");
            sql.append("email = ?");
            params.add(user.getEmail());
            first = false;
        }
        
        if (user.getPassword() != null) {
            if (!first) sql.append(", ");
            sql.append("password = ?");
            params.add(user.getPassword());
            first = false;
        }
        
        if (user.getFullName() != null) {
            if (!first) sql.append(", ");
            sql.append("full_name = ?");
            params.add(user.getFullName());
            first = false;
        }
        
        if (user.getPhone() != null) {
            if (!first) sql.append(", ");
            sql.append("phone = ?");
            params.add(user.getPhone());
            first = false;
        }
        
        if (first) {
            return false; // No fields to update
        }
        
        sql.append(", updated_at = CURRENT_TIMESTAMP WHERE user_id = ?");
        params.add(user.getUserId());
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}