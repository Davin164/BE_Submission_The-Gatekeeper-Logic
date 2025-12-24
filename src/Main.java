import com.sun.net.httpserver.HttpServer;
import controllers.BookingController;
import controllers.EventController;
import controllers.UserController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        try {
            int port = 8080;
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            
            // Register all controllers
            server.createContext("/api/bookings", new BookingController());
            server.createContext("/api/events", new EventController());
            server.createContext("/api/users", new UserController());
            
            server.setExecutor(null);
            server.start();
            
            System.out.println();
            System.out.println("  Event Ticketing Platform - Backend API");
            System.out.println();
            System.out.println();
            System.out.println("✓ Server started on port: " + port);
            System.out.println("✓ API Base URL: http://localhost:" + port);
            System.out.println();
            System.out.println();
            System.out.println("  BOOKINGS ENDPOINTS:");
            System.out.println();
            System.out.println("  GET    /api/bookings              - Get all bookings");
            System.out.println("  GET    /api/bookings/{id}         - Get booking by ID");
            System.out.println("  GET    /api/bookings/user/{id}    - Get user bookings");
            System.out.println("  GET    /api/bookings/event/{id}   - Get event bookings");
            System.out.println("  POST   /api/bookings              - Create booking");
            System.out.println("  PUT    /api/bookings/{id}/confirm - Confirm booking");
            System.out.println("  PUT    /api/bookings/{id}/cancel  - Cancel booking");
            System.out.println("  DELETE /api/bookings/{id}         - Delete booking");
            System.out.println();
            System.out.println();
            System.out.println("  EVENTS ENDPOINTS:");
            System.out.println();
            System.out.println("  GET    /api/events                - Get all events");
            System.out.println("  GET    /api/events/{id}           - Get event by ID");
            System.out.println("  POST   /api/events                - Create event");
            System.out.println("  PUT    /api/events/{id}           - Update event");
            System.out.println("  DELETE /api/events/{id}           - Delete event");
            System.out.println();
            System.out.println();
            System.out.println("  USERS ENDPOINTS:");
            System.out.println();
            System.out.println("  GET    /api/users                 - Get all users");
            System.out.println("  GET    /api/users/{id}            - Get user by ID");
            System.out.println("  POST   /api/users                 - Create user");
            System.out.println("  PUT    /api/users/{id}            - Update user");
            System.out.println("  DELETE /api/users/{id}            - Delete user");
            System.out.println();
            System.out.println();
            System.out.println("[INFO] Press Ctrl+C to stop the server");
            System.out.println();
            
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}