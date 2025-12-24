# EVENT TICKETING PLATFORM - COMPLETE API

## 📁 FILE STRUCTURE

```
src/
├── Main.java                          # Main entry point
├── controllers/
│   ├── BookingController.java         # Booking endpoints (CRUD)
│   ├── EventController.java           # Event endpoints (CRUD)
│   └── UserController.java            # User endpoints (CRUD)
├── services/
│   ├── BookingService.java            # Business logic for bookings
│   ├── EventService.java              # Business logic for events
│   └── UserService.java               # Business logic for users
├── dao/
│   ├── BookingDAO.java                # Database operations for bookings
│   ├── EventDAO.java                  # Database operations for events
│   └── UserDAO.java                   # Database operations for users
└── models/
    ├── Booking.java                   # Booking entity
    ├── Event.java                     # Event entity
    └── User.java                      # User entity
```

---

## 🚀 CARA INSTALL

### 1. Copy Semua File ke Project Kamu

Replace/update file-file berikut di project kamu:

- `src/Main.java`
- `src/controllers/` → copy semua file
- `src/services/` → copy semua file
- `src/dao/` → copy semua file
- `src/models/` → copy semua file

### 2. Compile

```bash
bash compile.sh
```

### 3. Run Server

```bash
bash run.sh
```

Server akan jalan di: **http://localhost:8080**

---

## 📋 API ENDPOINTS

### **USERS API**

#### 1. Get All Users
```http
GET http://localhost:8080/api/users
```

#### 2. Get User by ID
```http
GET http://localhost:8080/api/users/1
```

#### 3. Create User
```http
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phone": "081234567890",
  "role": "CUSTOMER"
}
```

#### 4. Update User
```http
PUT http://localhost:8080/api/users/1
Content-Type: application/json

{
  "fullName": "John Doe Updated",
  "phone": "081234567999"
}
```

#### 5. Delete User
```http
DELETE http://localhost:8080/api/users/1
```

---

### **EVENTS API**

#### 1. Get All Events
```http
GET http://localhost:8080/api/events
```

#### 2. Get Event by ID
```http
GET http://localhost:8080/api/events/1
```

#### 3. Create Event
```http
POST http://localhost:8080/api/events
Content-Type: application/json

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

#### 4. Update Event
```http
PUT http://localhost:8080/api/events/1
Content-Type: application/json

{
  "ticketPrice": 120000.00,
  "status": "ACTIVE"
}
```

#### 5. Delete Event
```http
DELETE http://localhost:8080/api/events/1
```

---

### **BOOKINGS API**

#### 1. Get All Bookings
```http
GET http://localhost:8080/api/bookings
```

#### 2. Get Booking by ID
```http
GET http://localhost:8080/api/bookings/1
```

#### 3. Get User Bookings
```http
GET http://localhost:8080/api/bookings/user/2
```

#### 4. Get Event Bookings
```http
GET http://localhost:8080/api/bookings/event/1
```

#### 5. Create Booking (WITH PESSIMISTIC LOCKING)
```http
POST http://localhost:8080/api/bookings
Content-Type: application/json

{
  "userId": 2,
  "eventId": 1,
  "quantity": 2
}
```

#### 6. Confirm Booking
```http
PUT http://localhost:8080/api/bookings/1/confirm
```

#### 7. Cancel Booking
```http
PUT http://localhost:8080/api/bookings/1/cancel
```

#### 8. Delete Booking
```http
DELETE http://localhost:8080/api/bookings/1
```

---

## ✅ FITUR YANG SUDAH LENGKAP

### ✓ USERS
- [x] GET all users
- [x] GET user by ID
- [x] POST create user
- [x] PUT update user (partial update)
- [x] DELETE user by ID

### ✓ EVENTS
- [x] GET all events
- [x] GET event by ID
- [x] POST create event
- [x] PUT update event (partial update)
- [x] DELETE event by ID

### ✓ BOOKINGS
- [x] GET all bookings
- [x] GET booking by ID
- [x] GET bookings by user ID
- [x] GET bookings by event ID
- [x] POST create booking (WITH LOCKING)
- [x] PUT confirm booking
- [x] PUT cancel booking
- [x] DELETE booking by ID

### ✓ SPECIAL FEATURES
- [x] **Pessimistic Locking** untuk prevent overselling
- [x] **Transaction Management** (BEGIN, COMMIT, ROLLBACK)
- [x] **Partial Update** (hanya update field yang dikirim)
- [x] **CORS Support** (bisa diakses dari frontend)
- [x] **Error Handling** yang proper

---

## 🧪 TESTING DI THUNDER CLIENT

### Setup di Thunder Client:

1. Buka Thunder Client di VSCode
2. Klik **New Request**
3. Pilih Method (GET/POST/PUT/DELETE)
4. Masukkan URL endpoint
5. Kalau POST/PUT, tambahkan Body (JSON)
6. Klik **Send**

### Example: Create Booking

**Method**: POST
**URL**: http://localhost:8080/api/bookings
**Headers**: 
```
Content-Type: application/json
```
**Body**:
```json
{
  "userId": 2,
  "eventId": 1,
  "quantity": 2
}
```

---

## 🔒 CARA KERJA PESSIMISTIC LOCKING

### Booking Flow dengan Locking:

```
1. User A request booking (quantity: 5)
   ↓
2. BEGIN TRANSACTION
   ↓
3. SELECT ... FOR UPDATE (LOCK ROW)
   ↓
4. Check available tickets (10)
   ↓
5. Insert booking
   ↓
6. Update available tickets (10 - 5 = 5)
   ↓
7. COMMIT (RELEASE LOCK)

User B request booking (simultaneously)
   ↓
Wait for User A's lock to release
   ↓
Get available tickets (5) ← Updated value
```

Ini mencegah **race condition** dan **overselling**!

---

## 📝 NOTES

1. **Password** di User belum di-hash (untuk production, gunakan BCrypt)
2. **Authentication** belum ada (tambahkan JWT kalau perlu)
3. **Validation** sudah ada di Service layer
4. **Error handling** sudah proper dengan try-catch

---

## 🐛 TROUBLESHOOTING

### Error: "Cannot find symbol"
```bash
# Pastikan compile ulang
bash compile.sh
```

### Error: "Connection refused"
```bash
# Cek MySQL sudah running
sudo systemctl status mysql

# Cek DatabaseConfig.java sudah benar
```

### Error: 404 Not Found
```bash
# Pastikan server sudah running
bash run.sh

# Cek URL endpoint sudah benar
```

---

## ✨ SUDAH SIAP PAKAI!

Semua file sudah **COMPLETE** dengan:
- ✅ Full CRUD operations
- ✅ Proper error handling
- ✅ Transaction management
- ✅ Pessimistic locking
- ✅ CORS support

**Tinggal copy ke project kamu dan jalankan!** 🚀

---

Created by: Claude AI Assistant
Date: December 2024