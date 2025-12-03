package com.hotel.system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Перевірка входу для Співробітника (Admin, Manager, Cleaner, Owner)
     * Використовує таблицю emp_main
     */
    public Map<String, Object> loginEmployee(String username, String password) {
        String sql = """
            SELECT e.employee_id, e.first_name, e.last_name, e.position, e.hotel_id 
            FROM emp_main em
            JOIN employees e ON em.e_id = e.employee_id
            WHERE em.username = ? AND em.password = ?
        """;

        try {
            return jdbcTemplate.queryForMap(sql, username, password);
        } catch (EmptyResultDataAccessException e) {
            return null; // Невірний логін або пароль
        }
    }

    /**
     * Перевірка входу для Клієнта
     * Використовує таблицю clients (пошук за телефоном)
     */
    public Map<String, Object> loginClient(String phone) {
        String sql = "SELECT * FROM clients WHERE phone = ?";

        try {
            return jdbcTemplate.queryForMap(sql, phone);
        } catch (EmptyResultDataAccessException e) {
            return null; // Клієнта не знайдено
        }
    }

    /**
     * Визначає системну роль (для Dynamic Routing) на основі посади співробітника
     */
    public String mapPositionToRole(String position) {
        if (position == null) return "GUEST";

        return switch (position.toLowerCase()) {
            case "admin", "receptionist", "адміністратор" -> "ADMIN";
            case "manager", "менеджер" -> "MANAGER";
            case "owner", "власник", "ceo" -> "OWNER";
            case "cleaner", "housekeeper", "прибиральниця" -> "CLEANER";
            case "accountant", "бухгалтер" -> "ACCOUNTANT";
            default -> "EMPLOYEE"; // Запасний варіант (можна мапити на role_employee)
        };
    }
}
