package services;

import dao.EventDAO;
import models.Event;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventService {
    private EventDAO eventDAO;
    
    public EventService() {
        this.eventDAO = new EventDAO();
    }
    
    public List<Event> getAllEvents() throws SQLException {
        return eventDAO.getAll();
    }
    
    public Event getEventById(int eventId) throws SQLException {
        return eventDAO.getById(eventId);
    }
    
    public Event createEvent(int organizerId, String eventName, String description,
                            String eventDate, String location, int totalCapacity, 
                            double ticketPrice) throws Exception {
        
        if (eventName == null || eventName.trim().isEmpty()) {
            throw new IllegalArgumentException("Event name is required");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }
        if (totalCapacity <= 0) {
            throw new IllegalArgumentException("Total capacity must be greater than 0");
        }
        if (ticketPrice < 0) {
            throw new IllegalArgumentException("Ticket price cannot be negative");
        }
        
        try {
            Event event = new Event();
            event.setOrganizerId(organizerId);
            event.setEventName(eventName);
            event.setDescription(description);
            
            // Parse date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            event.setEventDate(new Timestamp(sdf.parse(eventDate).getTime()));
            
            event.setLocation(location);
            event.setTotalCapacity(totalCapacity);
            event.setAvailableTickets(totalCapacity);
            event.setTicketPrice(new java.math.BigDecimal(ticketPrice));
            event.setStatus("ACTIVE");
            
            return eventDAO.create(event);
        } catch (Exception e) {
            throw new Exception("Failed to create event: " + e.getMessage());
        }
    }
    
    public boolean updateEvent(Event event) throws SQLException {
        return eventDAO.update(event);
    }
    
    public boolean deleteEvent(int eventId) throws SQLException {
        return eventDAO.delete(eventId);
    }
}