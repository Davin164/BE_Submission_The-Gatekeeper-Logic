#!/bin/bash

echo "Testing Event Ticketing Platform API"
echo "====================================="

# Test 1: Create Booking
echo "\n1. Creating booking..."
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "eventId": 1, "quantity": 3}'

# Test 2: Get Booking
echo "\n\n2. Getting booking by ID..."
curl -X GET http://localhost:8080/api/bookings/1

# Test 3: Get User Bookings
echo "\n\n3. Getting user bookings..."
curl -X GET http://localhost:8080/api/bookings/user/2

# Test 4: Concurrent Booking (Overselling Test)
echo "\n\n4. Testing concurrent bookings (overselling prevention)..."
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "eventId": 1, "quantity": 100}' &
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{"userId": 3, "eventId": 1, "quantity": 100}' &

wait
echo "\n\n✓ Tests completed!"