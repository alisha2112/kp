package com.hotel.system.controller;

import com.hotel.system.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // --- БРОНЮВАННЯ ---
    @GetMapping("/rooms/availability")
    public ResponseEntity<List<Map<String, Object>>> checkAvailability(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return ResponseEntity.ok(adminService.checkRoomAvailability(checkIn, checkOut));
    }

    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@RequestParam Long clientId, @RequestParam Long roomId,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                                           @RequestParam Integer guests, @RequestParam String paymentMethod) {
        Long id = adminService.createBooking(clientId, roomId, checkIn, checkOut, guests, paymentMethod);
        return ResponseEntity.ok(Map.of("bookingId", id));
    }

    @PutMapping("/bookings/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestParam Long roomId,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        adminService.updateBooking(id, roomId, checkIn, checkOut);
        return ResponseEntity.ok("Booking updated");
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, @RequestParam String reason) {
        adminService.cancelBooking(id, reason);
        return ResponseEntity.ok("Booking cancelled");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok(adminService.getReceptionDashboard());
    }

    // --- КЛІЄНТИ ---
    @PostMapping("/clients")
    public ResponseEntity<?> registerClient(@RequestParam String firstName, @RequestParam String middleName,
                                            @RequestParam String lastName, @RequestParam String phone, @RequestParam String email) {
        Long id = adminService.registerClient(firstName, middleName, lastName, phone, email);
        return ResponseEntity.ok(Map.of("clientId", id));
    }

    // --- ВИСЕЛЕННЯ ---
    @GetMapping("/rooms/{number}/status")
    public ResponseEntity<?> getRoomStatus(@PathVariable Integer number) {
        return ResponseEntity.ok(adminService.checkRoomStatus(number));
    }

    @GetMapping("/bookings/{id}/bill")
    public ResponseEntity<?> getBill(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.generateBill(id));
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestParam Long bookingId, @RequestParam String paymentMethod) {
        adminService.processCheckout(bookingId, paymentMethod);
        return ResponseEntity.ok("Checkout completed");
    }

    @PostMapping("/reviews/manual")
    public ResponseEntity<?> addManualReview(@RequestParam Long bookingId, @RequestParam Integer rating, @RequestParam String comment) {
        adminService.addReviewManually(bookingId, rating, comment);
        return ResponseEntity.ok("Review added");
    }

    // --- ОПЛАТА ---
    @PostMapping("/payments")
    public ResponseEntity<?> acceptPayment(@RequestParam Long bookingId, @RequestParam String lastName,
                                           @RequestParam String firstName, @RequestParam String middleName, @RequestParam String method) {
        adminService.acceptPayment(bookingId, lastName, firstName, middleName, method);
        return ResponseEntity.ok("Payment accepted");
    }

    @GetMapping("/bookings/{id}/debt")
    public ResponseEntity<?> checkDebt(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.checkDebt(id));
    }
}