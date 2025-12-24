package models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Booking {
    private int bookingId;
    private int userId;
    private int eventId;
    private String bookingCode;
    private int quantity;
    private BigDecimal totalPrice;
    private String status;
    private String paymentMethod;
    private Timestamp bookingDate;
    private Timestamp paymentDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public Booking() {}
    
    public Booking(int userId, int eventId, String bookingCode, int quantity, BigDecimal totalPrice) {
        this.userId = userId;
        this.eventId = eventId;
        this.bookingCode = bookingCode;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    
    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public Timestamp getBookingDate() { return bookingDate; }
    public void setBookingDate(Timestamp bookingDate) { this.bookingDate = bookingDate; }
    
    public Timestamp getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}