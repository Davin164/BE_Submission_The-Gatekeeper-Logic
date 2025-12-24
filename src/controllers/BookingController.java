package controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.BookingService;
import models.Booking;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BookingController implements HttpHandler {
    private BookingService bookingService;
    
    public BookingController() {
        this.bookingService = new BookingService();
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
        if (path.equals("/api/bookings")) {
            // GET all bookings
            List<Booking> bookings = bookingService.getAllBookings();
            String json = convertBookingsToJson(bookings);
            sendResponse(exchange, 200, json);
        } 
        else if (path.matches("/api/bookings/\\d+")) {
            // GET booking by ID
            int bookingId = extractIdFromPath(path);
            Booking booking = bookingService.getBookingById(bookingId);
            
            if (booking != null) {
                String json = convertBookingToJson(booking);
                sendResponse(exchange, 200, json);
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Booking not found\"}");
            }
        }
        else if (path.matches("/api/bookings/user/\\d+")) {
            // GET bookings by user
            int userId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            List<Booking> bookings = bookingService.getUserBookings(userId);
            String json = convertBookingsToJson(bookings);
            sendResponse(exchange, 200, json);
        }
        else if (path.matches("/api/bookings/event/\\d+")) {
            // GET bookings by event
            int eventId = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            List<Booking> bookings = bookingService.getEventBookings(eventId);
            String json = convertBookingsToJson(bookings);
            sendResponse(exchange, 200, json);
        }
        else {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }
    
    private void handlePost(HttpExchange exchange, String path) throws Exception {
        if (path.equals("/api/bookings")) {
            String body = readRequestBody(exchange);
            
            int userId = extractIntFromJson(body, "userId");
            int eventId = extractIntFromJson(body, "eventId");
            int quantity = extractIntFromJson(body, "quantity");
            
            Booking booking = bookingService.createBooking(userId, eventId, quantity);
            
            String json = convertBookingToJson(booking);
            sendResponse(exchange, 201, json);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }
    
    private void handlePut(HttpExchange exchange, String path) throws Exception {
        if (path.matches("/api/bookings/\\d+/confirm")) {
            String[] parts = path.split("/");
            int bookingId = Integer.parseInt(parts[3]);
            
            boolean updated = bookingService.updateBookingStatus(bookingId, "CONFIRMED");
            
            if (updated) {
                sendResponse(exchange, 200, "{\"message\": \"Booking confirmed successfully\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Booking not found\"}");
            }
        }
        else if (path.matches("/api/bookings/\\d+/cancel")) {
            String[] parts = path.split("/");
            int bookingId = Integer.parseInt(parts[3]);
            
            boolean updated = bookingService.updateBookingStatus(bookingId, "CANCELLED");
            
            if (updated) {
                sendResponse(exchange, 200, "{\"message\": \"Booking cancelled successfully\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Booking not found\"}");
            }
        }
        else {
            sendResponse(exchange, 404, "{\"error\": \"Not found\"}");
        }
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws Exception {
        if (path.matches("/api/bookings/\\d+")) {
            int bookingId = extractIdFromPath(path);
            boolean deleted = bookingService.deleteBooking(bookingId);
            
            if (deleted) {
                sendResponse(exchange, 200, "{\"message\": \"Booking deleted successfully\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Booking not found\"}");
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
    
    private String convertBookingToJson(Booking b) {
        return String.format(
            "{\"bookingId\":%d,\"userId\":%d,\"eventId\":%d,\"bookingCode\":\"%s\",\"quantity\":%d,\"totalPrice\":%.2f,\"status\":\"%s\",\"paymentMethod\":%s,\"bookingDate\":\"%s\",\"paymentDate\":%s}",
            b.getBookingId(), b.getUserId(), b.getEventId(), b.getBookingCode(),
            b.getQuantity(), b.getTotalPrice(), b.getStatus(),
            b.getPaymentMethod() != null ? "\"" + b.getPaymentMethod() + "\"" : "null",
            b.getBookingDate(),
            b.getPaymentDate() != null ? "\"" + b.getPaymentDate() + "\"" : "null"
        );
    }
    
    private String convertBookingsToJson(List<Booking> bookings) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < bookings.size(); i++) {
            json.append(convertBookingToJson(bookings.get(i)));
            if (i < bookings.size() - 1) json.append(",");
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