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

    // --- БРОНЮВАННЯ ТА ПЕРЕВІРКА НАЯВНОСТІ ---

    /**
     * Задача 1.1 з SQL-скрипту: Перевірка наявності номерів (get_available_rooms)
     * Використовується адміністратором, щоб побачити вільні кімнати на конкретні дати.
     */
    // --- БРОНЮВАННЯ ---
    @GetMapping("/rooms/availability")
    public ResponseEntity<List<Map<String, Object>>> checkAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            jakarta.servlet.http.HttpSession session) { // <-- Додаємо HttpSession

        // Дістаємо ID готелю з сесії поточного адміна
        Long hotelId = (Long) session.getAttribute("HOTEL_ID");

        if (hotelId == null) {
            // Якщо раптом ID немає (наприклад, сесія протухла або це супер-адмін без готелю)
            // Можна повернути помилку або порожній список
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(adminService.checkRoomAvailability(checkIn, checkOut, hotelId));
    }

    // ... (решта методів: createBooking, updateBooking, cancelBooking і т.д. залишаються без змін)

    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(
            @RequestParam Long clientId,
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam Integer guests,
            @RequestParam String paymentMethod) {

        try {
            // Викликаємо сервіс, який смикає процедуру в БД
            Long bookingId = adminService.createBooking(clientId, roomId, checkIn, checkOut, guests, paymentMethod);

            return ResponseEntity.ok(Map.of(
                    "message", "Booking created successfully",
                    "bookingId", bookingId
            ));
        } catch (Exception e) {
            // Якщо тригер в БД (check_room_availability) викине помилку про зайнятість
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
    public ResponseEntity<?> getDashboard(jakarta.servlet.http.HttpSession session) {
        // 1. Отримуємо ID готелю поточного адміністратора
        Long hotelId = (Long) session.getAttribute("HOTEL_ID");

        if (hotelId == null) {
            return ResponseEntity.badRequest().body("Hotel ID not found in session");
        }

        // 2. Передаємо його в сервіс
        return ResponseEntity.ok(adminService.getReceptionDashboard(hotelId));
    }

    // ... (інші методи для клієнтів, виселення, оплат залишаємо як у твоєму коді)
    @PostMapping("/clients")
    public ResponseEntity<?> registerClient(@RequestParam String firstName, @RequestParam String middleName,
                                            @RequestParam String lastName, @RequestParam String phone, @RequestParam String email) {
        Long id = adminService.registerClient(firstName, middleName, lastName, phone, email);
        return ResponseEntity.ok(Map.of("clientId", id));
    }

    // --- ВИСЕЛЕННЯ ТА СТАТУС ---
    @GetMapping("/rooms/{number}/status")
    public ResponseEntity<?> getRoomStatus(@PathVariable Integer number, jakarta.servlet.http.HttpSession session) {
        // 1. Отримуємо ID готелю
        Long hotelId = (Long) session.getAttribute("HOTEL_ID");
        if (hotelId == null) {
            return ResponseEntity.badRequest().body("Session error: No Hotel ID");
        }

        // 2. Викликаємо сервіс
        Map<String, Object> status = adminService.checkRoomStatus(number, hotelId);

        if (status == null) {
            return ResponseEntity.status(404).body("Room not found in this hotel");
        }

        return ResponseEntity.ok(status);
    }

    @GetMapping("/bookings/{id}/bill")
    public ResponseEntity<?> getBill(@PathVariable Long id, jakarta.servlet.http.HttpSession session) {
        // 1. Отримуємо ID готелю
        Long hotelId = (Long) session.getAttribute("HOTEL_ID");
        if (hotelId == null) {
            return ResponseEntity.badRequest().body("Session error");
        }

        // 2. Отримуємо дані рахунку
        List<Map<String, Object>> bill = adminService.generateBill(id, hotelId);

        // 3. Якщо список порожній, можливо, це чуже бронювання або його не існує
        if (bill.isEmpty()) {
            return ResponseEntity.status(404).body("Bill not found or access denied");
        }

        return ResponseEntity.ok(bill);
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