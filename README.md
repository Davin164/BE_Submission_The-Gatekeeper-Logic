# 🎫 THE GATEKEEPER LOGIC - Event Ticketing Platform

> **Backend Event Ticketing System** dengan Java Native + MySQL yang mencegah overselling

[![Java](https://img.shields.io/badge/Java-Native-orange.svg)](https://www.java.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)

**Dibuat oleh: Davin Gabriel J**  
**Submission: Backend Web Developer - The Gatekeeper Logic**

---

## 🎯 Tentang Project

Platform backend untuk sistem tiket event online yang memfasilitasi:
- **Organizer** untuk membuat dan mengelola event
- **Customer** untuk memesan tiket event
- **Sistem** untuk mencegah overselling dengan transaction locking

### Problem yang Diselesaikan
**Overselling Prevention**: Sistem menggunakan **Pessimistic Locking** untuk memastikan tidak ada tiket yang terjual melebihi kapasitas, bahkan ketika terjadi concurrent requests dari multiple users.

---

## ✨ Fitur Utama

### 🔒 Core Features
- ✅ **CRUD Users** - Manajemen data pengguna (Customer & Organizer)
- ✅ **CRUD Events** - Buat, update, dan kelola event
- ✅ **CRUD Bookings** - Sistem pemesanan tiket
- ✅ **Pessimistic Locking** - Mencegah race condition
- ✅ **Transaction Management** - ACID compliance
- ✅ **Partial Update** - Update field tertentu saja
- ✅ **Status Management** - Tracking status booking & event

### 🛡️ Security Features
- Database transaction dengan BEGIN/COMMIT/ROLLBACK
- SELECT FOR UPDATE untuk row-level locking
- Input validation di Service layer
- Error handling yang comprehensive

---

## 🏗️ Arsitektur & Database

### Struktur Project

```
src/
├── Main.java                    # Entry point & HTTP Server
├── controllers/
│   ├── UserController.java      # User endpoints
│   ├── EventController.java     # Event endpoints
│   └── BookingController.java   # Booking endpoints
├── services/
│   ├── UserService.java         # Business logic - Users
│   ├── EventService.java        # Business logic - Events
│   └── BookingService.java      # Business logic - Bookings
├── dao/
│   ├── UserDAO.java             # Database access - Users
│   ├── EventDAO.java            # Database access - Events
│   └── BookingDAO.java          # Database access - Bookings
└── models/
    ├── User.java                # User entity
    ├── Event.java               # Event entity
    └── Booking.java             # Booking entity
```

### Database Schema

#### Table: USERS
```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role ENUM('CUSTOMER', 'ORGANIZER') DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Table: EVENTS
```sql
CREATE TABLE events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    organizer_id INT NOT NULL,
    event_name VARCHAR(200) NOT NULL,
    description TEXT,
    event_date DATETIME NOT NULL,
    location VARCHAR(200) NOT NULL,
    total_capacity INT NOT NULL,
    available_tickets INT NOT NULL,
    ticket_price DECIMAL(10,2) NOT NULL,
    status ENUM('ACTIVE', 'CANCELLED', 'COMPLETED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES users(id)
);
```

#### Table: BOOKINGS
```sql
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') DEFAULT 'PENDING',
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (event_id) REFERENCES events(id)
);
```

---

## 🚀 Cara Install

### Prerequisites
- Java JDK 11+
- MySQL 8.0+
- Git

### Step 1: Clone Repository
```bash
git clone https://github.com/Davin164/BE_Submission_The-Gatekeeper-Logic.git
cd BE_Submission_The-Gatekeeper-Logic
```

### Step 2: Setup Database
```bash
# Login ke MySQL
mysql -u root -p

# Jalankan database.sql
source database.sql
```

### Step 3: Configure Environment
```bash
# Copy .env.example ke .env
cp .env.example .env

# Edit .env sesuai konfigurasi MySQL kamu
nano .env
```

Isi `.env`:
```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=event_ticketing
DB_USER=root
DB_PASSWORD=your_password
```

### Step 4: Compile & Run
```bash
# Compile
bash compile.sh

# Run server
bash run.sh
```

Server akan berjalan di: **http://localhost:8080**

---

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

---

### 👤 USERS ENDPOINTS

#### 1. Get All Users
```http
GET /api/users
```

**Response Success (200):**
```json
[
  {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "phone": "081234567890",
    "role": "CUSTOMER",
    "createdAt": "2024-12-24T10:30:00"
  }
]
```

#### 2. Get User by ID
```http
GET /api/users/{id}
```

#### 3. Create User
```http
POST /api/users
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phone": "081234567890",
  "role": "CUSTOMER"
}
```

**Response Success (201):**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "CUSTOMER"
}
```

#### 4. Update User
```http
PUT /api/users/{id}
Content-Type: application/json
```

**Request Body (Partial):**
```json
{
  "fullName": "John Doe Updated",
  "phone": "081234567999"
}
```

#### 5. Delete User
```http
DELETE /api/users/{id}
```

---

### 🎪 EVENTS ENDPOINTS

#### 1. Get All Events
```http
GET /api/events
```

**Response Success (200):**
```json
[
  {
    "id": 1,
    "organizerId": 1,
    "eventName": "Music Festival 2025",
    "description": "Amazing music festival",
    "eventDate": "2025-06-15T18:00:00",
    "location": "Jakarta Convention Center",
    "totalCapacity": 5000,
    "availableTickets": 4500,
    "ticketPrice": 150000.00,
    "status": "ACTIVE",
    "createdAt": "2024-12-24T10:00:00"
  }
]
```

#### 2. Get Event by ID
```http
GET /api/events/{id}
```

#### 3. Create Event
```http
POST /api/events
Content-Type: application/json
```

**Request Body:**
```json
{
  "organizerId": 1,
  "eventName": "Music Festival 2025",
  "description": "Amazing music festival",
  "eventDate": "2025-06-15 18:00:00",
  "location": "Jakarta Convention Center",
  "totalCapacity": 5000,
  "ticketPrice": 150000.00
}
```

**Response Success (201):**
```json
{
  "id": 1,
  "eventName": "Music Festival 2025",
  "availableTickets": 5000,
  "status": "ACTIVE"
}
```

#### 4. Update Event
```http
PUT /api/events/{id}
Content-Type: application/json
```

**Request Body (Partial):**
```json
{
  "ticketPrice": 120000.00,
  "status": "ACTIVE"
}
```

#### 5. Delete Event
```http
DELETE /api/events/{id}
```

---

### 🎟️ BOOKINGS ENDPOINTS

#### 1. Get All Bookings
```http
GET /api/bookings
```

#### 2. Get Booking by ID
```http
GET /api/bookings/{id}
```

#### 3. Get User Bookings
```http
GET /api/bookings/user/{userId}
```

#### 4. Get Event Bookings
```http
GET /api/bookings/event/{eventId}
```

#### 5. Create Booking (WITH LOCKING) 🔒
```http
POST /api/bookings
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": 2,
  "eventId": 1,
  "quantity": 2
}
```

**Response Success (201):**
```json
{
  "id": 1,
  "userId": 2,
  "eventId": 1,
  "quantity": 2,
  "totalPrice": 300000.00,
  "status": "PENDING",
  "bookingDate": "2024-12-24T14:30:00"
}
```

**Response Error (400):**
```json
{
  "error": "Not enough tickets available"
}
```

#### 6. Confirm Booking
```http
PUT /api/bookings/{id}/confirm
```

#### 7. Cancel Booking
```http
PUT /api/bookings/{id}/cancel
```

#### 8. Delete Booking
```http
DELETE /api/bookings/{id}
```

---

## 🧪 Testing

### Testing dengan Thunder Client (VSCode)

1. Install extension **Thunder Client**
2. Import collection atau create new request
3. Set method dan URL
4. Untuk POST/PUT, tambahkan body JSON
5. Klik **Send**

### Testing Script

Gunakan script yang sudah disediakan:

```bash
# Test semua endpoints
bash test_api.sh
```

### Manual Testing Examples

#### Test Overselling Prevention
```bash
# Terminal 1: Booking 100 tiket
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "eventId": 1, "quantity": 100}'

# Terminal 2: Booking 100 tiket (simultaneous)
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{"userId": 3, "eventId": 1, "quantity": 100}'
```

## 🛠️ Technology Stack

- **Language**: Java (Native) JDK16
- **Database**: MySQL 8.0+
- **Run API Test**: Thunder Client (extension VSCode)

---

## 👨‍💻 Author

**Davin Gabriel J**
- GitHub: [@Davin164](https://github.com/Davin164)
---

## 🙏 Acknowledgments

- Submission untuk Backend Web Developer Program
- Studi Kasus: "The Gatekeeper Logic"
- Platform Tiket Event Online dengan Overselling Prevention
---

**Made with ☕ and 💻 in Indonesia**
*Last Updated: 24 December 2025*
