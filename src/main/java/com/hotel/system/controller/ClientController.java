package com.hotel.system.controller;

import com.hotel.system.service.ClientService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    private Long getClientId(HttpSession session) {
        Object id = session.getAttribute("USER_ID");
        if (id == null) throw new RuntimeException("User not logged in");
        return ((Number) id).longValue();
    }

    // --- 1. ПРОФІЛЬ ---
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestParam String firstName, @RequestParam String lastName,
                                           @RequestParam String phone, @RequestParam String email, HttpSession session) {
        clientService.updateProfile(getClientId(session), firstName, lastName, phone, email);
        return ResponseEntity.ok("Profile updated");
    }

    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteAccount(HttpSession session) {
        clientService.deleteAccount(getClientId(session));
        session.invalidate();
        return ResponseEntity.ok("Account deleted");
    }

    // --- 2. БРОНЮВАННЯ ---
    @GetMapping("/rooms/search")
    public ResponseEntity<List<Map<String, Object>>> searchRooms(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                                                                 @RequestParam(required = false) String city) {
        return ResponseEntity.ok(clientService.searchRoomsForClient(checkIn, checkOut, city));
    }

    @PostMapping("/bookings")
    public ResponseEntity<?> bookRoom(@RequestParam Long roomId,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                                      @RequestParam Integer guests, HttpSession session) {
        Long id = clientService.bookRoom(getClientId(session), roomId, checkIn, checkOut, guests);
        return ResponseEntity.ok(Map.of("bookingId", id));
    }

    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, @RequestParam String reason, HttpSession session) {
        clientService.cancelBooking(getClientId(session), id, reason);
        return ResponseEntity.ok("Cancelled");
    }

    @PostMapping("/bookings/{id}/repeat")
    public ResponseEntity<?> repeatBooking(@PathVariable Long id,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                                           HttpSession session) {
        Long newId = clientService.repeatBooking(getClientId(session), id, checkIn, checkOut);
        return ResponseEntity.ok(Map.of("newBookingId", newId));
    }

    @GetMapping("/bookings/history")
    public ResponseEntity<List<Map<String, Object>>> getHistory(HttpSession session) {
        return ResponseEntity.ok(clientService.getBookingHistory(getClientId(session)));
    }

    // --- 3. ОПЛАТА ---
    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestParam Long bookingId, @RequestParam String cardToken, HttpSession session) {
        clientService.payOnline(getClientId(session), bookingId, cardToken);
        return ResponseEntity.ok("Paid");
    }

    @GetMapping("/payments/history")
    public ResponseEntity<List<Map<String, Object>>> getPaymentHistory(HttpSession session) {
        return ResponseEntity.ok(clientService.getPaymentHistory(getClientId(session)));
    }

    // --- 4. МАРКЕТИНГ ---
    @PostMapping("/promocode")
    public ResponseEntity<?> usePromocode(@RequestParam String code, HttpSession session) {
        return ResponseEntity.ok(Map.of("discount", clientService.usePromocode(getClientId(session), code)));
    }

    @GetMapping("/promotions")
    public ResponseEntity<List<Map<String, Object>>> getPromotions() {
        return ResponseEntity.ok(clientService.getActivePromotions());
    }

    // --- 6. ПОСЛУГИ ---
    @PostMapping("/services/request")
    public ResponseEntity<?> requestService(@RequestParam Long bookingId, @RequestParam Long serviceId, HttpSession session) {
        clientService.requestService(getClientId(session), bookingId, serviceId);
        return ResponseEntity.ok("Service requested");
    }

    @PostMapping("/food/order")
    public ResponseEntity<?> orderFood(@RequestParam Long roomId, @RequestBody String itemsJson, HttpSession session) {
        clientService.orderFood(getClientId(session), roomId, itemsJson);
        return ResponseEntity.ok("Food ordered");
    }

    // --- 7. ІНШЕ ---
    @PostMapping("/reviews")
    public ResponseEntity<?> leaveReview(@RequestParam Long bookingId, @RequestParam Integer rating, @RequestParam String comment, HttpSession session) {
        clientService.leaveReview(getClientId(session), bookingId, rating, comment);
        return ResponseEntity.ok("Review added");
    }

    @PostMapping("/rooms/{id}/favorite")
    public ResponseEntity<?> addFavorite(@PathVariable Long id, HttpSession session) {
        clientService.addToFavorites(getClientId(session), id);
        return ResponseEntity.ok("Added to favorites");
    }
}