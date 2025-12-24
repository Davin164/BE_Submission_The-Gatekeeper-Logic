package services;

import dao.BookingDAO;
import dao.EventDAO;
import models.Booking;
import models.Event;

import java.sql.SQLException;
import java.util.List;

public class BookingService {
    private BookingDAO bookingDAO;
    private EventDAO eventDAO;
    
    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.eventDAO = new EventDAO();
    }
    
    public List<Booking> getAllBookings() throws SQLException {
        return bookingDAO.getAll();
    }
    
    public Booking createBooking(int userId, int eventId, int quantity) throws Exception {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        Event event = eventDAO.getById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        try {
            return bookingDAO.createBookingWithLock(userId, eventId, quantity);
        } catch (SQLException e) {
            throw new Exception("Failed to create booking: " + e.getMessage());
        }
    }
    
    public Booking getBookingById(int bookingId) throws SQLException {
        return bookingDAO.getById(bookingId);
    }
    
    public List<Booking> getUserBookings(int userId) throws SQLException {
        return bookingDAO.getByUserId(userId);
    }
    
    public List<Booking> getEventBookings(int eventId) throws SQLException {
        return bookingDAO.getByEventId(eventId);
    }
    
    public boolean updateBookingStatus(int bookingId, String status) throws Exception {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid booking status");
        }
        
        try {
            return bookingDAO.updateStatus(bookingId, status);
        } catch (SQLException e) {
            throw new Exception("Failed to update booking status: " + e.getMessage());
        }
    }
    
    public boolean deleteBooking(int bookingId) throws SQLException {
        return bookingDAO.delete(bookingId);
    }
    
    private boolean isValidStatus(String status) {
        return status.equals("PENDING") || status.equals("CONFIRMED") || 
               status.equals("CANCELLED") || status.equals("EXPIRED");
    }
}