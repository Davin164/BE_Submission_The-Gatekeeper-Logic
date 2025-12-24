package controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.EventService;
import models.Event;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EventController implements HttpHandler {
    private EventService eventService;
    
    public EventController() {
        this.eventService = new EventService();
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
        if (path.equals("/api/events")) {
            // GET all events
            List<Event> events = eventService.getAllEvents();
            String json = convertEventsToJson(events);
            sendResponse(exchange, 200, json);
        } 
        else if (path.matches("/api/events/\\d+")) {
            // GET event by ID
            int eventId = extractIdFromPath(path);
            Event event = eventService.getEventById(eventId);
            
            if (event != null) {
                String json = convertEventToJson(event);
                sendResponse(exchange, 200, json);
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Event not found\"}");
            }
        }
        else {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }
    
    private void handlePost(HttpExchange exchange, String path) throws Exception {
        if (path.equals("/api/events")) {
            String body = readRequestBody(exchange);
            
            int organizerId = extractIntFromJson(body, "organizerId");
            String eventName = extractStringFromJson(body, "eventName");
            String description = extractStringFromJson(body, "description");
            String eventDate = extractStringFromJson(body, "eventDate");
            String location = extractStringFromJson(body, "location");
            int totalCapacity = extractIntFromJson(body, "totalCapacity");
            double ticketPrice = extractDoubleFromJson(body, "ticketPrice");
            
            Event event = eventService.createEvent(organizerId, eventName, description, 
                                                   eventDate, location, totalCapacity, ticketPrice);
            
            String json = convertEventToJson(event);
            sendResponse(exchange, 201, json);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }
    
    private void handlePut(HttpExchange exchange, String path) throws Exception {
        if (path.matches("/api/events/\\d+")) {
            int eventId = extractIdFromPath(path);
            String body = readRequestBody(exchange);
            
            Event event = new Event();
            event.setEventId(eventId);
            
            // Optional fields
            if (body.contains("eventName")) {
                event.setEventName(extractStringFromJson(body, "eventName"));
            }
            if (body.contains("description")) {
                event.setDescription(extractStringFromJson(body, "description"));
            }
            if (body.contains("location")) {
                event.setLocation(extractStringFromJson(body, "location"));
            }
            if (body.contains("ticketPrice")) {
                event.setTicketPrice(new java.math.BigDecimal(extractDoubleFromJson(body, "ticketPrice")));
            }
            if (body.contains("status")) {
                event.setStatus(extractStringFromJson(body, "status"));
            }
            
            boolean updated = eventService.updateEvent(event);
            
            if (updated) {
                sendResponse(exchange, 200, "{\"message\": \"Event updated successfully\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Event not found\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws Exception {
        if (path.matches("/api/events/\\d+")) {
            int eventId = extractIdFromPath(path);
            boolean deleted = eventService.deleteEvent(eventId);
            
            if (deleted) {
                sendResponse(exchange, 200, "{\"message\": \"Event deleted successfully\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Event not found\"}");
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
    
    private int extractIntFromJson(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        throw new IllegalArgumentException("Missing field: " + key);
    }
    
    private double extractDoubleFromJson(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*([\\d.]+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Double.parseDouble(m.group(1));
        }
        throw new IllegalArgumentException("Missing field: " + key);
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
    
    private String convertEventToJson(Event e) {
        return String.format(
            "{\"eventId\":%d,\"organizerId\":%d,\"eventName\":\"%s\",\"description\":\"%s\",\"eventDate\":\"%s\",\"location\":\"%s\",\"totalCapacity\":%d,\"availableTickets\":%d,\"ticketPrice\":%.2f,\"status\":\"%s\"}",
            e.getEventId(), e.getOrganizerId(), e.getEventName(), 
            e.getDescription() != null ? e.getDescription() : "",
            e.getEventDate(), e.getLocation(), e.getTotalCapacity(), 
            e.getAvailableTickets(), e.getTicketPrice(), e.getStatus()
        );
    }
    
    private String convertEventsToJson(List<Event> events) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < events.size(); i++) {
            json.append(convertEventToJson(events.get(i)));
            if (i < events.size() - 1) json.append(",");
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