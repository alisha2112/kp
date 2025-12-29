package com.hotel.system.controller;

import com.hotel.system.config.routing.DbContextHolder;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Це автоматично підключить JdbcTemplate через конструктор
public class AuthController {

    private final JdbcTemplate jdbcTemplate;

    @Value("${hotel.datasource.url}")
    private String dbUrl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session) {

        // 1. Спроба підключення до БД (це те, що створює сесію в pgAdmin)
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {

            // ВИЗНАЧАЄМО РОЛЬ
            String role = determineRole(username);

            // Зберігаємо дані в сесії для Interceptor
            session.setAttribute("DB_USER", username);
            session.setAttribute("DB_PASS", password);
            session.setAttribute("CURRENT_ROLE", role);

            // 2. ОТРИМУЄМО ВНУТРІШНІ ID (щоб не було "нулів" у звітах та помилок 500)
            if ("CLIENT".equals(role)) {
                // Для клієнта шукаємо в таблиці клієнтів
                String sql = "SELECT client_id FROM clients LIMIT 1";
                try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                    if (rs.next()) {
                        session.setAttribute("USER_ID", rs.getLong("client_id"));
                    }
                }
            } else if (!"GUEST".equals(role) && !"OWNER".equals(role)) {
                // Для працівників (адмін, менеджер, прибиральник, бухгалтер)
                String sql = "SELECT employee_id, hotel_id FROM employees WHERE position ILIKE ? LIMIT 1";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "%" + role + "%");
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        session.setAttribute("USER_ID", rs.getLong("employee_id"));
                        session.setAttribute("HOTEL_ID", rs.getLong("hotel_id"));
                    }
                }
            }

            // 3. ПЕРЕМИКАЄМО КОНТЕКСТ ДЛЯ DATASOURCE
            // Тепер змінна 'role' точно доступна тут
            DbContextHolder.setRole(role);

            // У вашому AuthController.java всередині методу login:

// ... після DbContextHolder.setRole(role);

// Примусово робимо запит до бази саме через Routed DataSource
            try {
                jdbcTemplate.execute("SELECT 1");
                System.out.println(">>> [DB] Connection triggered for user: " + username);
            } catch (Exception e) {
                System.out.println(">>> [DB] Force connect failed: " + e.getMessage());
            }

//            // 4. ПРИМУСОВИЙ ЗАПИТ (щоб з'єднання миттєво з'явилося в pgAdmin)
//            try {
//                jdbcTemplate.execute("SELECT 1");
//            } catch (Exception e) {
//                System.out.println("Wait for pool: " + e.getMessage());
//            }

            return ResponseEntity.ok(Map.of(
                    "message", "Database connection established",
                    "role", role
            ));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Database access denied: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        DbContextHolder.clear();
        return ResponseEntity.ok("Disconnected from Database");
    }

    private String determineRole(String username) {
        String u = username.toLowerCase();
        if (u.contains("admin")) return "ADMIN";
        if (u.contains("manager")) return "MANAGER";
        if (u.contains("owner")) return "OWNER";
        if (u.contains("accountant")) return "ACCOUNTANT";
        if (u.contains("cleaner")) return "CLEANER";
        if (u.contains("client")) return "CLIENT";
        return "GUEST";
    }
}

