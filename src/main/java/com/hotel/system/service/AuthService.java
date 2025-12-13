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

    public Map<String, Object> universalLogin(String phone) {
        // 1. Спочатку шукаємо серед СПІВРОБІТНИКІВ (через безпечну функцію)
        // ВИПРАВЛЕНО: Використовуємо функцію замість прямого SELECT
        String empSql = "SELECT * FROM sp_login_employee_by_phone(?)";

        try {
            Map<String, Object> emp = jdbcTemplate.queryForMap(empSql, phone);

            String position = (String) emp.get("position");
            String role = mapPositionToRole(position);

            return Map.of(
                    "type", "EMPLOYEE",
                    "id", emp.get("employee_id"),
                    "name", emp.get("first_name") + " " + emp.get("last_name"),
                    "role", role,
                    "hotel_id", emp.get("hotel_id")
            );
        } catch (EmptyResultDataAccessException e) {
            // Не знайшли серед співробітників, йдемо далі
        }

        // 2. Шукаємо серед КЛІЄНТІВ (ця функція sp_login_client вже працює)
        String clientSql = "SELECT * FROM sp_login_client(?)";
        try {
            Map<String, Object> client = jdbcTemplate.queryForMap(clientSql, phone);

            return Map.of(
                    "type", "CLIENT",
                    "id", client.get("client_id"),
                    "name", client.get("first_name") + " " + client.get("last_name"),
                    "role", "CLIENT",
                    "hotel_id", 0
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private String mapPositionToRole(String position) {
        if (position == null) return "GUEST";
        String pos = position.toLowerCase();

        if (pos.contains("admin") || pos.contains("адміністратор")) return "ADMIN";
        if (pos.contains("manager") || pos.contains("менеджер")) return "MANAGER";
        if (pos.contains("owner") || pos.contains("власник")) return "OWNER";
        if (pos.contains("cleaner") || pos.contains("прибиральниця")) return "CLEANER";
        if (pos.contains("accountant") || pos.contains("бухгалтер")) return "ACCOUNTANT";

        return "EMPLOYEE";
    }
}
