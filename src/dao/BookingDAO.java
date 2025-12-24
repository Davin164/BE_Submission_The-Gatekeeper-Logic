package dao;

import config.DatabaseConfig;
import models.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    
    public List<Booking> getAll() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        }
        return bookings;
    }
    
    public Booking createBookingWithLock(int userId, int eventId, int quantity) throws SQLException {
        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Step 1: SELECT FOR UPDATE - Pessimistic Lock
            String selectSQL = "SELECT event_id, event_name, available_tickets, ticket_price, status " +
                             "FROM events WHERE event_id = ? FOR UPDATE";
            selectStmt = conn.prepareStatement(selectSQL);
            selectStmt.setInt(1, eventId);
            rs = selectStmt.executeQuery();
            
            if (!rs.next()) {
                throw new SQLException("Event not found");
            }
            
            int availableTickets = rs.getInt("available_tickets");
            String status = rs.getString("status");
            java.math.BigDecimal ticketPrice = rs.getBigDecimal("ticket_price");
            
            if (!"ACTIVE".equals(status)) {
                throw new SQLException("Event is not active");
            }
            
            if (availableTickets < quantity) {
                throw new SQLException("Insufficient tickets. Available: " + availableTickets);
            }
            
            String bookingCode = generateBookingCode();
            java.math.BigDecimal totalPrice = ticketPrice.multiply(new java.math.BigDecimal(quantity));
            
            // Step 2: Insert booking
            String insertSQL = "INSERT INTO bookings (user_id, event_id, booking_code, quantity, total_price, status) " +
                             "VALUES (?, ?, ?, ?, ?, 'PENDING')";
            insertStmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, eventId);
            insertStmt.setString(3, bookingCode);
            insertStmt.setInt(4, quantity);
            insertStmt.setBigDecimal(5, totalPrice);
            insertStmt.executeUpdate();
            
            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            int bookingId = 0;
            if (generatedKeys.next()) {
                bookingId = generatedKeys.getInt(1);
            }
            
            // Step 3: Update available tickets
            String updateSQL = "UPDATE events SET available_tickets = available_tickets - ? WHERE event_id = ?";
            updateStmt = conn.prepareStatement(updateSQL);
            updateStmt.setInt(1, quantity);
            updateStmt.setInt(2, eventId);
            updateStmt.executeUpdate();
            
            conn.commit();
            
            Booking booking = new Booking(userId, eventId, bookingCode, quantity, totalPrice);
            booking.setBookingId(bookingId);
            
            return booking;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (selectStmt != null) selectStmt.close();
            if (insertStmt != null) insertStmt.close();
            if (updateStmt != null) updateStmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    private String generateBookingCode() {
        return "BK" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
    
    public Booking getById(int bookingId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBooking(rs);
            }
            return null;
        }
    }
    
    public List<Booking> getByUserId(int userId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        }
        return bookings;
    }
    
    public List<Booking> getByEventId(int eventId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE event_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        }
        return bookings;
    }
    
    public boolean updateStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE bookings SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE booking_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int bookingId) throws SQLException {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setEventId(rs.getInt("event_id"));
        booking.setBookingCode(rs.getString("booking_code"));
        booking.setQuantity(rs.getInt("quantity"));
        booking.setTotalPrice(rs.getBigDecimal("total_price"));
        booking.setStatus(rs.getString("status"));
        booking.setPaymentMethod(rs.getString("payment_method"));
        booking.setBookingDate(rs.getTimestamp("booking_date"));
        booking.setPaymentDate(rs.getTimestamp("payment_date"));
        booking.setCreatedAt(rs.getTimestamp("created_at"));
        booking.setUpdatedAt(rs.getTimestamp("updated_at"));
        return booking;
    }
}