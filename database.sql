CREATE DATABASE IF NOT EXISTS be_submissionmember_soal2_davingabrieljonathan;
USE be_submissionmember_soal2_davingabrieljonathan;

-- Table: users
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role ENUM('ORGANIZER', 'CUSTOMER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: events
CREATE TABLE events (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    organizer_id INT NOT NULL,
    event_name VARCHAR(200) NOT NULL,
    description TEXT,
    event_date DATETIME NOT NULL,
    location VARCHAR(200) NOT NULL,
    total_capacity INT NOT NULL,
    available_tickets INT NOT NULL,
    ticket_price DECIMAL(10, 2) NOT NULL,
    status ENUM('ACTIVE', 'CLOSED', 'CANCELLED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES users(user_id),
    CHECK (available_tickets >= 0),
    CHECK (available_tickets <= total_capacity)
);

-- Table: bookings
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    booking_code VARCHAR(50) UNIQUE NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED') DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (event_id) REFERENCES events(event_id),
    CHECK (quantity > 0)
);

-- Indexes for performance
CREATE INDEX idx_events_organizer ON events(organizer_id);
CREATE INDEX idx_events_status ON events(status);
CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_event ON bookings(event_id);
CREATE INDEX idx_bookings_code ON bookings(booking_code);

-- Sample Data
INSERT INTO users (username, email, password, full_name, phone, role) VALUES
('organizer1', 'organizer@example.com', 'hashed_password', 'John Organizer', '081234567890', 'ORGANIZER'),
('customer1', 'customer@example.com', 'hashed_password', 'Jane Customer', '081234567891', 'CUSTOMER');