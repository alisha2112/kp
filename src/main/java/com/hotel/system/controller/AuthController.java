package com.hotel.system.controller;

import com.hotel.system.config.routing.ClientRoutingDataSource;
import com.hotel.system.config.routing.DbContextHolder;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext; // Додано
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource; // Додано
import java.sql.*;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JdbcTemplate jdbcTemplate;
    private final ApplicationContext context; // Використовуємо контекст для безпечного отримання DataSource

    @Value("${hotel.datasource.url}")
    private String dbUrl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session) {

        // 1. Спроба прямого підключення до БД для валідації
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {

            String role = determineRole(username);

            // --- ВИПРАВЛЕННЯ ПОМИЛКИ CANNOT FIND SYMBOL ---
            // Отримуємо наш DataSource з контексту Spring
            DataSource dataSource = context.getBean(DataSource.class);

            if (dataSource instanceof ClientRoutingDataSource) {
                ClientRoutingDataSource routingDS = (ClientRoutingDataSource) dataSource;

                // Якщо це не системна роль (ADMIN/MANAGER і т.д.), а динамічний клієнт
                // перевіряємо, чи є для нього вже створений пул
                if (!routingDS.hasDataSource(username)) {
                    routingDS.addDataSource(username, createSpecificPool(username, password));
                    System.out.println(">>> [DYNAMIC POOL] Created new pool for user: " + username);
                }
            }
            // ----------------------------------------------

            // Зберігаємо дані в сесії
            session.setAttribute("DB_USER", username);
            session.setAttribute("DB_PASS", password);
            session.setAttribute("CURRENT_ROLE", username);

            // 2. ОТРИМУЄМО ВНУТРІШНІЙ ID КЛІЄНТА
            // Шукаємо за новим полем db_username
            String sql = "SELECT client_id FROM clients WHERE db_username = ? OR db_username = lower(?) LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        session.setAttribute("USER_ID", rs.getLong("client_id"));
                    }
                }
            }

            // 3. ПЕРЕМИКАЄМО КОНТЕКСТ
            DbContextHolder.setRole(username);

            // Примусовий запит для активації з'єднання в pgAdmin
            try {
                jdbcTemplate.execute("SELECT 1");
            } catch (Exception e) {
                System.out.println("Wait for dynamic pool: " + e.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Database connection established as " + username,
                    "role", role
            ));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Database access denied: " + e.getMessage());
        }
    }

    // Допоміжний метод для створення пулу
    private DataSource createSpecificPool(String user, String pass) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(user);
        config.setPassword(pass);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMinimumIdle(0);
        config.setMaximumPoolSize(2);
        config.setIdleTimeout(30000);
        config.addDataSourceProperty("ApplicationName", "HotelApp_User_" + user);
        return new HikariDataSource(config);
    }

    private String determineRole(String username) {
        String u = username.toLowerCase();
        if (u.contains("admin")) return "ADMIN";
        if (u.contains("manager")) return "MANAGER";
        if (u.contains("owner")) return "OWNER";
        if (u.contains("accountant")) return "ACCOUNTANT";
        if (u.contains("cleaner")) return "CLEANER";
        return "CLIENT"; // Якщо не системний - значить клієнт
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        DbContextHolder.clear();
        return ResponseEntity.ok("Disconnected");
    }
}




//package com.hotel.system.controller;
//
//import com.hotel.system.config.routing.ClientRoutingDataSource;
//import com.hotel.system.config.routing.DbContextHolder;
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.web.bind.annotation.*;
//
//import javax.sql.DataSource;
//import java.sql.*;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor // Це автоматично підключить JdbcTemplate через конструктор
//public class AuthController {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    @Value("${hotel.datasource.url}")
//    private String dbUrl;
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
//        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {
//
//            String role = determineRole(username);
//
//            // 1. ДИНАМІЧНО СТВОРЮЄМО ПУЛ ДЛЯ НОВОГО КОРИСТУВАЧА (щоб бачити в pgAdmin)
//            if (dataSource instanceof ClientRoutingDataSource) {
//                ClientRoutingDataSource routingDS = (ClientRoutingDataSource) dataSource;
//                // Якщо для цього конкретного юзера ще немає пулу в мапі - створюємо його
//                if (!routingDS.getResolvedDataSources().containsKey(username)) {
//                    routingDS.addDataSource(username, createSpecificPool(username, password));
//                }
//            }
//
//            // 2. Використовуємо логін як ключ для перемикання
//            session.setAttribute("DB_USER", username);
//            session.setAttribute("DB_PASS", password);
//            session.setAttribute("CURRENT_ROLE", username); // Ключем тепер є сам USERNAME
//
//            // 3. Отримуємо ID саме цього клієнта
//            String sql = "SELECT client_id FROM clients WHERE db_username = ? OR db_username = lower(?) LIMIT 1";
//            try (PreparedStatement ps = conn.prepareStatement(sql)) {
//                ps.setString(1, username);
//                ps.setString(2, username);
//                ResultSet rs = ps.executeQuery();
//                if (rs.next()) {
//                    session.setAttribute("USER_ID", rs.getLong("client_id"));
//                }
//            }
//
//            // Перемикаємо контекст і робимо запит
//            DbContextHolder.setRole(username);
//            jdbcTemplate.execute("SELECT 1");
//
//            return ResponseEntity.ok(Map.of("message", "Connected as " + username, "role", role));
//
//        } catch (SQLException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied: " + e.getMessage());
//        }
//    }

//    // Допоміжний метод для створення пулу на льоту
//    private DataSource createSpecificPool(String user, String pass) {
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl(dbUrl);
//        config.setUsername(user);
//        config.setPassword(pass);
//        config.setDriverClassName("org.postgresql.Driver");
//        config.setMinimumIdle(0);
//        config.setMaximumPoolSize(2);
//        config.addDataSourceProperty("ApplicationName", "HotelApp_User_" + user);
//        return new HikariDataSource(config);
//    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestParam String username,
//                                   @RequestParam String password,
//                                   HttpSession session) {
//
//        // 1. Спроба підключення до БД (це те, що створює сесію в pgAdmin)
//        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {
//
//            // ВИЗНАЧАЄМО РОЛЬ
//            String role = determineRole(username);
//
//            // Зберігаємо дані в сесії для Interceptor
//            session.setAttribute("DB_USER", username);
//            session.setAttribute("DB_PASS", password);
//            session.setAttribute("CURRENT_ROLE", role);
//
//            // 2. ОТРИМУЄМО ВНУТРІШНІ ID (щоб не було "нулів" у звітах та помилок 500)
//            if ("CLIENT".equals(role)) {
//                // Для клієнта шукаємо в таблиці клієнтів
//                String sql = "SELECT client_id FROM clients LIMIT 1";
//                try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
//                    if (rs.next()) {
//                        session.setAttribute("USER_ID", rs.getLong("client_id"));
//                    }
//                }
//            } else if (!"GUEST".equals(role) && !"OWNER".equals(role)) {
//                // Для працівників (адмін, менеджер, прибиральник, бухгалтер)
//                String sql = "SELECT employee_id, hotel_id FROM employees WHERE position ILIKE ? LIMIT 1";
//                try (PreparedStatement ps = conn.prepareStatement(sql)) {
//                    ps.setString(1, "%" + role + "%");
//                    ResultSet rs = ps.executeQuery();
//                    if (rs.next()) {
//                        session.setAttribute("USER_ID", rs.getLong("employee_id"));
//                        session.setAttribute("HOTEL_ID", rs.getLong("hotel_id"));
//                    }
//                }
//            }
//
//            // 3. ПЕРЕМИКАЄМО КОНТЕКСТ ДЛЯ DATASOURCE
//            // Тепер змінна 'role' точно доступна тут
//            DbContextHolder.setRole(role);
//
//            // У вашому AuthController.java всередині методу login:
//
//// ... після DbContextHolder.setRole(role);
//
//// Примусово робимо запит до бази саме через Routed DataSource
//            try {
//                jdbcTemplate.execute("SELECT 1");
//                System.out.println(">>> [DB] Connection triggered for user: " + username);
//            } catch (Exception e) {
//                System.out.println(">>> [DB] Force connect failed: " + e.getMessage());
//            }
//
////            // 4. ПРИМУСОВИЙ ЗАПИТ (щоб з'єднання миттєво з'явилося в pgAdmin)
////            try {
////                jdbcTemplate.execute("SELECT 1");
////            } catch (Exception e) {
////                System.out.println("Wait for pool: " + e.getMessage());
////            }
//
//            return ResponseEntity.ok(Map.of(
//                    "message", "Database connection established",
//                    "role", role
//            ));
//
//        } catch (SQLException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Database access denied: " + e.getMessage());
//        }
//    }


//
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpSession session) {
//        session.invalidate();
//        DbContextHolder.clear();
//        return ResponseEntity.ok("Disconnected from Database");
//    }
//
//    private String determineRole(String username) {
//        String u = username.toLowerCase();
//        if (u.contains("admin")) return "ADMIN";
//        if (u.contains("manager")) return "MANAGER";
//        if (u.contains("owner")) return "OWNER";
//        if (u.contains("accountant")) return "ACCOUNTANT";
//        if (u.contains("cleaner")) return "CLEANER";
//        if (u.contains("client")) return "CLIENT";
//        return "GUEST";
//    }
//}

