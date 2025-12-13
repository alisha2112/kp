package com.hotel.system.controller;

import com.hotel.system.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String phone, HttpSession session) {
        // 1. Знаходимо користувача в БД
        Map<String, Object> user = authService.universalLogin(phone);

        if (user != null) {
            String role = (String) user.get("role");

            // 2. Зберігаємо дані в сесії
            // Це критичний момент: саме тут ми кажемо системі, ким бути далі.
            session.setAttribute("CURRENT_ROLE", role);
            session.setAttribute("USER_ID", user.get("id"));

            // Якщо це співробітник, зберігаємо ID готелю
            if ("EMPLOYEE".equals(user.get("type"))) {
                session.setAttribute("HOTEL_ID", user.get("hotel_id"));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "role", role,
                    "user", user
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found via phone number");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out");
    }
}