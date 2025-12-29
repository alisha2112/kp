package com.hotel.system.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${hotel.datasource.url}")
    private String dbUrl;

    // Цей метод відповідає на POST запит від форми логіна
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session) {

        // Спроба підключитися до бази даних з введеними користувачем даними
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {

            // Якщо SQLException не виникло — пароль вірний!

            // Зберігаємо дані в сесії, щоб Interceptor міг їх дістати для кожного наступного запиту
            session.setAttribute("DB_USER", username);
            session.setAttribute("DB_PASS", password);

            // Визначаємо роль для перенаправлення в браузері
            String role = determineRole(username);
            session.setAttribute("CURRENT_ROLE", role);

            return ResponseEntity.ok(Map.of(
                    "message", "Database connection established",
                    "role", role,
                    "dbUser", username
            ));

        } catch (SQLException e) {
            // Якщо база відмовила в доступі
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Database access denied: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Disconnected from Database");
    }

    // Допоміжний метод для визначення ролі за іменем користувача БД
    private String determineRole(String username) {
        if (username.contains("admin")) return "ADMIN";
        if (username.contains("manager")) return "MANAGER";
        if (username.contains("owner")) return "OWNER";
        if (username.contains("accountant")) return "ACCOUNTANT";
        if (username.contains("cleaner")) return "CLEANER";
        if (username.contains("client")) return "CLIENT";
        return "GUEST";
    }
}

//import com.hotel.system.service.AuthService;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthService authService;
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestParam String phone, HttpSession session) {
//        // 1. Знаходимо користувача в БД
//        Map<String, Object> user = authService.universalLogin(phone);
//
//        if (user != null) {
//            String role = (String) user.get("role");
//
//            // 2. Зберігаємо дані в сесії
//            // Це критичний момент: саме тут ми кажемо системі, ким бути далі.
//            session.setAttribute("CURRENT_ROLE", role);
//            session.setAttribute("USER_ID", user.get("id"));
//
//            // Якщо це співробітник, зберігаємо ID готелю
//            if ("EMPLOYEE".equals(user.get("type"))) {
//                session.setAttribute("HOTEL_ID", user.get("hotel_id"));
//            }
//
//            return ResponseEntity.ok(Map.of(
//                    "message", "Login successful",
//                    "role", role,
//                    "user", user
//            ));
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found via phone number");
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpSession session) {
//        session.invalidate();
//        return ResponseEntity.ok("Logged out");
//    }
//}