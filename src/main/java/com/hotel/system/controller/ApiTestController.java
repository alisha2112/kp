package com.hotel.system.controller;

import com.hotel.system.config.routing.DbRole;
import com.hotel.system.service.AdminService;
import com.hotel.system.service.ClientService;
import com.hotel.system.service.ManagementService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiTestController {

    private final ClientService clientService;
    private final AdminService adminService;
    private final ManagementService managementService;

    // 1. АВТОРИЗАЦІЯ (Симуляція)
    @PostMapping("/login")
    public String login(@RequestParam String role, HttpSession session) {
        // Зберігаємо роль в сесії. Interceptor підхопить її при наступних запитах.
        session.setAttribute("CURRENT_ROLE", role.toUpperCase());
        return "Logged in as " + role.toUpperCase();
    }

    // 2. ТЕСТИ КЛІЄНТА (Role: CLIENT або GUEST)
    @GetMapping("/public/search")
    public Object searchRooms(@RequestParam String city, @RequestParam String start, @RequestParam String end) {
        return clientService.searchRooms(city, LocalDate.parse(start), LocalDate.parse(end), 1);
    }

    @PostMapping("/client/register")
    public Long registerSelf(@RequestParam String name, @RequestParam String phone, @RequestParam String email) {
        return clientService.register(name, "Fatherovich", "Lastname", phone, email);
    }

    @PostMapping("/client/book")
    public Long bookRoom(@RequestParam Long clientId, @RequestParam Long roomId, @RequestParam String start, @RequestParam String end) {
        return clientService.bookRoom(clientId, roomId, LocalDate.parse(start), LocalDate.parse(end), 1);
    }

    @PostMapping("/client/pay")
    public String payBooking(@RequestParam Long clientId, @RequestParam Long bookingId) {
        clientService.payForBooking(clientId, bookingId, "VISA-1234");
        return "Payment Successful";
    }

    // 3. ТЕСТИ АДМІНА (Role: ADMIN)
    @GetMapping("/admin/bookings")
    public Object getReceptionBookings() {
        return adminService.getActiveBookings();
    }

    @PostMapping("/admin/check-out")
    public String checkOut(@RequestParam Long bookingId) {
        adminService.checkOut(bookingId, "cash");
        return "Checkout Completed";
    }

    @GetMapping("/admin/room-status")
    public Object getRoomStatus(@RequestParam Integer roomNumber) {
        return adminService.getRoomStatus(roomNumber);
    }

    // 4. ТЕСТИ ВЛАСНИКА/МЕНЕДЖЕРА (Role: OWNER / MANAGER)
    @GetMapping("/owner/roi")
    public Object getRoi(@RequestParam(required = false) Long hotelId) {
        return managementService.getRoiAnalysis(hotelId);
    }

    @GetMapping("/manager/finance")
    public Object getFinanceReport(@RequestParam Long hotelId, @RequestParam String start, @RequestParam String end) {
        return managementService.getFinancialReport(LocalDate.parse(start), LocalDate.parse(end), hotelId);
    }
}