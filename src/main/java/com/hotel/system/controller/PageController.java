package com.hotel.system.controller;

import com.hotel.system.service.ClientService;
import com.hotel.system.service.CommonService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final ClientService clientService;
    private final CommonService commonService; // Додали CommonService для послуг

//    // --- Головна + Каталог готелів ---
//    @GetMapping("/")
//    public String home(Model model) {
//        model.addAttribute("hotels", clientService.getPublicHotelCatalog());
//        return "home";
//    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // Логіка перенаправлення (щоб адмін не сидів на головній клієнта)
        String role = (String) session.getAttribute("CURRENT_ROLE");
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) {
            return "redirect:/admin/dashboard";
        }

        // Завантаження каталогу (це те, що викликало помилку)
        model.addAttribute("hotels", clientService.getPublicHotelCatalog());
        return "home"; // або "home", як називається ваш HTML файл
    }



    @GetMapping("/search")
    public String searchRooms(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "1") Integer guests,
            @RequestParam(required = false) String comfortLevel,
            Model model) {

        // ВАРІАНТ А: Використовуємо ОДНУ універсальну функцію для всіх.
        // Вона тепер повертає room_id, тому бронювання працюватиме.
        List<Map<String, Object>> rooms = clientService.searchRoomsPublic(city, checkIn, checkOut, guests, comfortLevel);

        model.addAttribute("rooms", rooms);

        // Параметри для форми
        model.addAttribute("city", city);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("guests", guests);

        return "search-results";
    }

    // --- Сторінка Послуг (GET /services) ---
    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", commonService.getHotelServices());
        return "services"; // створимо services.html
    }

    // --- Сторінка Відгуків (GET /reviews/{hotelName}) ---
    @GetMapping("/reviews/{hotelName}")
    public String reviews(@PathVariable String hotelName, Model model) {
        model.addAttribute("reviews", clientService.getHotelReviews(hotelName));
        model.addAttribute("hotelName", hotelName);
        return "reviews"; // створимо reviews.html
    }

    // --- Auth ---
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // --- Особистий кабінет (Профіль) ---
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        // 1. Перевіряємо, чи залогінений користувач
        Object userId = session.getAttribute("USER_ID");

        if (userId == null) {
            return "redirect:/login"; // Якщо ні - на вхід
        }

        String role = (String) session.getAttribute("CURRENT_ROLE");
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) {
            return "redirect:/admin/dashboard"; // Адмін лізе в профіль -> в адмінку
        }

        // 2. Отримуємо ID
        Long clientId = ((Number) userId).longValue();

        // 3. Завантажуємо дані через наш новий безпечний метод (Stored Function)
        Map<String, Object> client = clientService.getClientProfile(clientId);

        model.addAttribute("client", client);
        return "profile";
    }

    @GetMapping("/my-bookings")
    public String myBookings(HttpSession session, Model model) {
        Object userId = session.getAttribute("USER_ID");
        if (userId == null) return "redirect:/login";

        // Захист
        String role = (String) session.getAttribute("CURRENT_ROLE");
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) return "redirect:/admin/dashboard";

        Long clientId = ((Number) userId).longValue();

        List<Map<String, Object>> bookings = clientService.getBookingHistory(clientId);
        List<Map<String, Object>> services = clientService.getAllServices();

        // --- ДОДАЛИ МЕНЮ ---
        List<Map<String, Object>> menu = clientService.getMenu();

        model.addAttribute("bookings", bookings);
        model.addAttribute("services", services);
        model.addAttribute("menu", menu); // <--- Передаємо в HTML

        return "my-bookings";
    }
    // ...

    // Сторінка ПІДТВЕРДЖЕННЯ бронювання (GET /client/book)
    @GetMapping("/client/book")
    public String bookRoomConfirm(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam Integer guests,
            HttpSession session,
            Model model) {

        // 1. Перевірка авторизації
        if (session.getAttribute("USER_ID") == null) {
            return "redirect:/login";
        }

        // Захист
        String role = (String) session.getAttribute("CURRENT_ROLE");
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) return "redirect:/admin/dashboard";

        // 2. Отримуємо деталі кімнати через репозиторій (або сервіс)
        // Нам потрібно знати назву готелю, номер кімнати та ціну за ніч
        // Якщо у вас немає окремого методу в Service, можна викликати repository напряму або додати метод getRoomDetails(roomId)
        // Тут припускаємо, що метод повертає Map з ключами: hotel_name, room_number, price_per_night, comfort_level
        Map<String, Object> room = clientService.getRoomDetails(roomId);

        // 3. Розрахунок повної вартості
        java.math.BigDecimal pricePerNight = (java.math.BigDecimal) room.get("price_per_night");
        long days = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        java.math.BigDecimal totalPrice = pricePerNight.multiply(java.math.BigDecimal.valueOf(days));

        // 4. Передаємо дані в шаблон
        model.addAttribute("room", room);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("guests", guests);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("days", days);

        return "booking-confirm";
    }

    // ...

    // --- Історія оплат ---
    @GetMapping("/payments/history")
    public String paymentHistory(HttpSession session, Model model) {
        Object userId = session.getAttribute("USER_ID");
        if (userId == null) return "redirect:/login";

        // Захист
        String role = (String) session.getAttribute("CURRENT_ROLE");
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) return "redirect:/admin/dashboard";

        Long clientId = ((Number) userId).longValue();

        // Викликаємо сервіс (який викликає SQL функцію get_client_payment_history)
        List<Map<String, Object>> payments = clientService.getPaymentHistory(clientId);

        model.addAttribute("payments", payments);
        return "payment-history"; // Створимо цей файл
    }

    // ...

    @GetMapping("/promotions")
    public String promotions(HttpSession session, Model model) {
        Object userId = session.getAttribute("USER_ID");
        if (userId == null) return "redirect:/login";

        // Захист
        String role = (String) session.getAttribute("CURRENT_ROLE");
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) return "redirect:/admin/dashboard";

        // Цей метод тепер поверне реальні дані з бази
        model.addAttribute("promos", clientService.getActivePromotions());

        return "promotions";
    }

    @GetMapping("/food")
    public String foodPage(HttpSession session, Model model) {
        Object userId = session.getAttribute("USER_ID");
        if (userId == null) return "redirect:/login";

        // Захист
        String role = (String) session.getAttribute("CURRENT_ROLE");
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) return "redirect:/admin/dashboard";

        Long clientId = ((Number) userId).longValue();

        // Передаємо меню
        model.addAttribute("menu", clientService.getMenu());

        // Передаємо активні кімнати (для випадаючого списку "Куди доставити")
        model.addAttribute("activeRooms", clientService.getActiveRooms(clientId));

        return "food"; // Назва HTML файлу (food.html)
    }

    @GetMapping("/reviews")
    public String reviewsPage(HttpSession session, Model model) {
        Object userId = session.getAttribute("USER_ID");
        if (userId == null) return "redirect:/login";

        // Захист
        String role = (String) session.getAttribute("CURRENT_ROLE");
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) return "redirect:/admin/dashboard";

        Long clientId = ((Number) userId).longValue();

        // Отримуємо список
        List<Map<String, Object>> list = clientService.getBookingsForReview(clientId);
        model.addAttribute("bookingsToReview", list);

        return "client-reviews";
    }

    @GetMapping("/favorites")
    public String favoritesPage(HttpSession session, Model model) {
        Object userId = session.getAttribute("USER_ID");
        if (userId == null) return "redirect:/login";

        // Захист
        String role = (String) session.getAttribute("CURRENT_ROLE");
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) return "redirect:/admin/dashboard";

        Long clientId = ((Number) userId).longValue();

        // Отримуємо список улюблених номерів
        List<Map<String, Object>> favorites = clientService.getFavoriteRooms(clientId);
        model.addAttribute("favorites", favorites);

        return "favorites"; // Ім'я шаблону
    }

    // ... інші методи ...

//    @GetMapping("/admin/dashboard")
//    public String adminDashboard(HttpSession session) {
//        // 1. Перевірка авторизації
//        if (session.getAttribute("USER_ID") == null) return "redirect:/login";
////
////        // Захист
////        String role = (String) session.getAttribute("CURRENT_ROLE");
////        if ("ADMIN".equals(role) || "MANAGER".equals(role)) return "redirect:/admin/dashboard";
//
//        // 2. Перевірка ролі (ЗАХИСТ ВІД КЛІЄНТІВ)
//        String role = (String) session.getAttribute("CURRENT_ROLE");
//        if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
//            return "redirect:/profile";
//        }
//
//        return "admin-dashboard";
//    }
}
