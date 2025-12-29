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

        // 1. Спроба підключення до БД для перевірки реальних кредових даних (Login Role)
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {

            // 2. ДИНАМІЧНІ ПУЛИ (для відображення реального імені в pgAdmin)
            DataSource dataSource = context.getBean(DataSource.class);
            if (dataSource instanceof ClientRoutingDataSource) {
                ClientRoutingDataSource routingDS = (ClientRoutingDataSource) dataSource;

                // Якщо для цього конкретного імені (наприклад, alina_2025) ще немає пулу — додаємо його
                if (!routingDS.hasDataSource(username)) {
                    routingDS.addDataSource(username, createSpecificPool(username, password));
                    System.out.println(">>> [DYNAMIC POOL] Created for: " + username);
                }
            }

            // Змінна для визначення логіки перенаправлення в UI
            String logicalRole = "GUEST";

            // 3. ПЕРЕВІРКА: ХТО ЦЕЙ КОРИСТУВАЧ У СИСТЕМІ?

            // Крок А: Шукаємо в таблиці ПРАЦІВНИКІВ
            String empSql = "SELECT employee_id, hotel_id, position FROM employees WHERE db_username = lower(?) LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(empSql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Це ПРАЦІВНИК
                        long empId = rs.getLong("employee_id");
                        long hotelId = rs.getLong("hotel_id");
                        String position = rs.getString("position").toLowerCase();

                        session.setAttribute("USER_ID", empId);
                        session.setAttribute("HOTEL_ID", hotelId);

                        // Визначаємо логічну роль за посадою для контролерів сторінок
                        if (position.contains("admin") || position.contains("адмін")) logicalRole = "ADMIN";
                        else if (position.contains("manager") || position.contains("менедж")) logicalRole = "MANAGER";
                        else if (position.contains("cleaner") || position.contains("прибир")) logicalRole = "CLEANER";
                        else if (position.contains("accountant") || position.contains("бухг")) logicalRole = "ACCOUNTANT";
                        else if (position.contains("owner") || position.contains("власник")) logicalRole = "OWNER";
                    }
                    else {
                        // Крок Б: Якщо не знайшли в працівниках, шукаємо в КЛІЄНТАХ
                        String clientSql = "SELECT client_id FROM clients WHERE db_username = lower(?) LIMIT 1";
                        try (PreparedStatement ps2 = conn.prepareStatement(clientSql)) {
                            ps2.setString(1, username);
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                if (rs2.next()) {
                                    // Це КЛІЄНТ
                                    session.setAttribute("USER_ID", rs2.getLong("client_id"));
                                    logicalRole = "CLIENT";
                                }
                            }
                        }
                    }
                }
            }

            // 4. ЗБЕРІГАЄМО ДАНІ В СЕСІЇ ДЛЯ INTERCEPTOR ТА UI
            session.setAttribute("DB_USER", username);
            session.setAttribute("DB_PASS", password);
            session.setAttribute("CURRENT_ROLE", logicalRole); // "ADMIN", "CLIENT" тощо
            session.setAttribute("LOGICAL_ROLE", logicalRole);

            // 5. ПЕРЕМИКАЄМО КОНТЕКСТ НА КОНКРЕТНОГО ЮЗЕРА (для маршрутизації пулів)
            DbContextHolder.setRole(username);

            // Примусовий запит, щоб з'єднання миттєво з'явилося в pgAdmin Dashboard
            try {
                jdbcTemplate.execute("SELECT 1");
            } catch (Exception e) {
                System.out.println(">>> [DB] Trace: " + e.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Database connection established",
                    "role", logicalRole,
                    "dbUser", username
            ));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Database access denied: " + e.getMessage());
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
//        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {
//
//            // 1. Створюємо динамічний пул (як раніше)
//            DataSource dataSource = context.getBean(DataSource.class);
//            if (dataSource instanceof ClientRoutingDataSource) {
//                ClientRoutingDataSource routingDS = (ClientRoutingDataSource) dataSource;
//                if (!routingDS.hasDataSource(username)) {
//                    routingDS.addDataSource(username, createSpecificPool(username, password));
//                }
//            }
//
//            String logicalRole = "GUEST"; // За замовчуванням
//
//            // 2. ШУКАЄМО В ТАБЛИЦІ ПРАЦІВНИКІВ (за db_username)
//            String empSql = "SELECT employee_id, hotel_id, position FROM employees WHERE db_username = lower(?) LIMIT 1";
//            try (PreparedStatement ps = conn.prepareStatement(empSql)) {
//                ps.setString(1, username);
//                ResultSet rs = ps.executeQuery();
//                if (rs.next()) {
//                    session.setAttribute("USER_ID", rs.getLong("employee_id"));
//                    session.setAttribute("HOTEL_ID", rs.getLong("hotel_id"));
//
//                    // Визначаємо логічну роль за реальною посадою з бази
//                    String pos = rs.getString("position").toLowerCase();
//                    if (pos.contains("admin") || pos.contains("адмін")) logicalRole = "ADMIN";
//                    else if (pos.contains("manager") || pos.contains("менедж")) logicalRole = "MANAGER";
//                    else if (pos.contains("cleaner") || pos.contains("прибир")) logicalRole = "CLEANER";
//                    else if (pos.contains("accountant") || pos.contains("бухг")) logicalRole = "ACCOUNTANT";
//                } else {
//                    // 3. ЯКЩО НЕ ПРАЦІВНИК, ШУКАЄМО В ТАБЛИЦІ КЛІЄНТІВ
//                    String clientSql = "SELECT client_id FROM clients WHERE db_username = lower(?) LIMIT 1";
//                    try (PreparedStatement ps2 = conn.prepareStatement(clientSql)) {
//                        ps2.setString(1, username);
//                        ResultSet rs2 = ps2.executeQuery();
//                        if (rs2.next()) {
//                            session.setAttribute("USER_ID", rs2.getLong("client_id"));
//                            logicalRole = "CLIENT";
//                        }
//                    }
//                }
//            }
//
//            // 4. ОСТАТОЧНЕ НАЛАШТУВАННЯ СЕСІЇ
//            session.setAttribute("DB_USER", username);
//            session.setAttribute("DB_PASS", password);
//            session.setAttribute("CURRENT_ROLE", logicalRole); // Тепер тут точно "ADMIN" для нового адміна
//
//            DbContextHolder.setRole(username);
//            jdbcTemplate.execute("SELECT 1");
//
//            return ResponseEntity.ok(Map.of("message", "Success", "role", logicalRole));
//
//        } catch (SQLException e) {
//            return ResponseEntity.status(401).body("DB Error: " + e.getMessage());
//        }
//    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestParam String username,
//                                   @RequestParam String password,
//                                   HttpSession session) {
//
//        // 1. Спроба підключення до БД для перевірки логіна/пароля
//        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {
//
//            // Визначаємо ЛОГІЧНУ роль (ADMIN, MANAGER, CLIENT тощо)
//            // Ця змінна тепер оголошується ОДИН раз на початку
//            String logicalRole = determineRole(username);
//
//            // 2. ДИНАМІЧНІ ПУЛИ (для відображення в pgAdmin)
//            DataSource dataSource = context.getBean(DataSource.class);
//            if (dataSource instanceof ClientRoutingDataSource) {
//                ClientRoutingDataSource routingDS = (ClientRoutingDataSource) dataSource;
//
//                // Якщо для цього юзера (username) ще немає пулу в мапі — додаємо його
//                if (!routingDS.hasDataSource(username)) {
//                    routingDS.addDataSource(username, createSpecificPool(username, password));
//                }
//            }
//
//            // 3. ЗБЕРІГАЄМО ДАНІ В СЕСІЇ
//            // Зберігаємо реальне ім'я користувача БД для перехоплювача (Interceptor)
//            session.setAttribute("DB_USER", username);
//            session.setAttribute("DB_PASS", password);
//
//            // Зберігаємо логічну роль для AdminPageController (щоб пускало в дашборд)
//            session.setAttribute("CURRENT_ROLE", logicalRole);
//            session.setAttribute("LOGICAL_ROLE", logicalRole);
//
//            // 4. ОТРИМУЄМО ВНУТРІШНІ ID (USER_ID та HOTEL_ID)
//            if ("CLIENT".equals(logicalRole)) {
//                // Для клієнта шукаємо за новим полем db_username
//                String sql = "SELECT client_id FROM clients WHERE db_username = ? OR db_username = lower(?) LIMIT 1";
//                try (PreparedStatement ps = conn.prepareStatement(sql)) {
//                    ps.setString(1, username);
//                    ps.setString(2, username);
//                    try (ResultSet rs = ps.executeQuery()) {
//                        if (rs.next()) {
//                            session.setAttribute("USER_ID", rs.getLong("client_id"));
//                        }
//                    }
//                }
//            } else if (!"GUEST".equals(logicalRole) && !"OWNER".equals(logicalRole)) {
//                // Для працівників (ADMIN, MANAGER, CLEANER, ACCOUNTANT)
//                String sql = "SELECT employee_id, hotel_id FROM employees WHERE position ILIKE ? LIMIT 1";
//                try (PreparedStatement ps = conn.prepareStatement(sql)) {
//                    ps.setString(1, "%" + logicalRole + "%");
//                    try (ResultSet rs = ps.executeQuery()) {
//                        if (rs.next()) {
//                            session.setAttribute("USER_ID", rs.getLong("employee_id"));
//                            session.setAttribute("HOTEL_ID", rs.getLong("hotel_id"));
//                        }
//                    }
//                }
//            }
//
//            // 5. ПЕРЕМИКАЄМО КОНТЕКСТ НА USERNAME (щоб у pgAdmin було видно саме його)
//            DbContextHolder.setRole(username);
//
//            // Примусовий запит, щоб з'єднання миттєво з'явилося в pgAdmin
//            try {
//                jdbcTemplate.execute("SELECT 1");
//            } catch (Exception e) {
//                System.out.println(">>> [DB] Trace: " + e.getMessage());
//            }
//
//            return ResponseEntity.ok(Map.of(
//                    "message", "Database connection established",
//                    "role", logicalRole,
//                    "dbUser", username
//            ));
//
//        } catch (SQLException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Database access denied: " + e.getMessage());
//        }
//    }

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
