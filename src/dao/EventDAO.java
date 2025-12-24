package dao;

import config.DatabaseConfig;
import models.Event;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    
    public List<Event> getAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY event_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }
    
    public Event getById(int eventId) throws SQLException {
        String sql = "SELECT * FROM events WHERE event_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEvent(rs);
            }
            return null;
        }
    }
    
    public Event create(Event event) throws SQLException {
        String sql = "INSERT INTO events (organizer_id, event_name, description, event_date, location, " +
                    "total_capacity, available_tickets, ticket_price, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, event.getOrganizerId());
            stmt.setString(2, event.getEventName());
            stmt.setString(3, event.getDescription());
            stmt.setTimestamp(4, event.getEventDate());
            stmt.setString(5, event.getLocation());
            stmt.setInt(6, event.getTotalCapacity());
            stmt.setInt(7, event.getAvailableTickets());
            stmt.setBigDecimal(8, event.getTicketPrice());
            stmt.setString(9, event.getStatus());
            
            stmt.executeUpdate();
            
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                event.setEventId(generatedKeys.getInt(1));
            }
            
            return event;
        }
    }
    
    public boolean update(Event event) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE events SET ");
        List<Object> params = new ArrayList<>();
        boolean first = true;
        
        if (event.getEventName() != null) {
            if (!first) sql.append(", ");
            sql.append("event_name = ?");
            params.add(event.getEventName());
            first = false;
        }
        
        if (event.getDescription() != null) {
            if (!first) sql.append(", ");
            sql.append("description = ?");
            params.add(event.getDescription());
            first = false;
        }
        
        if (event.getLocation() != null) {
            if (!first) sql.append(", ");
            sql.append("location = ?");
            params.add(event.getLocation());
            first = false;
        }
        
        if (event.getTicketPrice() != null) {
            if (!first) sql.append(", ");
            sql.append("ticket_price = ?");
            params.add(event.getTicketPrice());
            first = false;
        }
        
        if (event.getStatus() != null) {
            if (!first) sql.append(", ");
            sql.append("status = ?");
            params.add(event.getStatus());
            first = false;
        }
        
        if (first) {
            return false; // No fields to update
        }
        
        sql.append(", updated_at = CURRENT_TIMESTAMP WHERE event_id = ?");
        params.add(event.getEventId());
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int eventId) throws SQLException {
        String sql = "DELETE FROM events WHERE event_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("event_id"));
        event.setOrganizerId(rs.getInt("organizer_id"));
        event.setEventName(rs.getString("event_name"));
        event.setDescription(rs.getString("description"));
        event.setEventDate(rs.getTimestamp("event_date"));
        event.setLocation(rs.getString("location"));
        event.setTotalCapacity(rs.getInt("total_capacity"));
        event.setAvailableTickets(rs.getInt("available_tickets"));
        event.setTicketPrice(rs.getBigDecimal("ticket_price"));
        event.setStatus(rs.getString("status"));
        event.setCreatedAt(rs.getTimestamp("created_at"));
        event.setUpdatedAt(rs.getTimestamp("updated_at"));
        return event;
    }
}