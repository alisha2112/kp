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

    @PostMapping("/login/employee")
    public ResponseEntity<?> loginEmployee(@RequestParam String username,
                                           @RequestParam String password,
                                           HttpSession session) {
        Map<String, Object> emp = authService.loginEmployee(username, password);
        if (emp != null) {
            String position = (String) emp.get("position");
            String role = authService.mapPositionToRole(position);

            // Зберігаємо в сесії для Interceptor-а
            session.setAttribute("CURRENT_ROLE", role);
            session.setAttribute("USER_ID", emp.get("employee_id"));
            session.setAttribute("HOTEL_ID", emp.get("hotel_id"));

            return ResponseEntity.ok(Map.of("message", "Login successful", "role", role, "user", emp));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @PostMapping("/login/client")
    public ResponseEntity<?> loginClient(@RequestParam String phone, HttpSession session) {
        Map<String, Object> client = authService.loginClient(phone);
        if (client != null) {
            // Клієнт завжди має роль CLIENT
            session.setAttribute("CURRENT_ROLE", "CLIENT");
            session.setAttribute("USER_ID", client.get("client_id"));

            return ResponseEntity.ok(Map.of("message", "Login successful", "role", "CLIENT", "user", client));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client not found");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // Очищаємо сесію
        return ResponseEntity.ok("Logged out");
    }
}