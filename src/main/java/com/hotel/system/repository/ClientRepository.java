package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ClientRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long registerClient(String firstName, String middleName, String lastName, String phone, String email, boolean isSelfRegistration) {
        String procName = isSelfRegistration ? "sp_self_register" : "sp_register_client";

        // Явно використовуємо синтаксис CALL для PostgreSQL
        String sql = "CALL " + procName + "(?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.execute(connection -> {
            CallableStatement cs = connection.prepareCall(sql);
            cs.setString(1, firstName);
            cs.setString(2, middleName);
            cs.setString(3, lastName);
            cs.setString(4, phone);
            cs.setString(5, email);

            cs.registerOutParameter(6, Types.BIGINT);
            cs.setNull(6, Types.BIGINT);

            return cs;
        }, (CallableStatementCallback<Long>) cs -> {
            cs.execute();
            return cs.getLong(6); // Отримуємо повернутий ID
        });
    }

    /** 1.1 Оновлення профілю */
    public void updateProfile(Long clientId, String firstName, String lastName, String phone, String email) {
        // Для void процедур (які нічого не повертають) достатньо update
        jdbcTemplate.update("CALL sp_client_update_profile(?, ?, ?, ?, ?)", clientId, firstName, lastName, phone, email);
    }

    /** 1.2 Видалення профілю */
    public void deleteAccount(Long clientId) {
        jdbcTemplate.update("CALL sp_client_delete_account(?)", clientId);
    }

    public Map<String, Object> getClientById(Long clientId) {
        // Викликаємо збережену функцію бази даних
        String sql = "SELECT * FROM get_client_profile_data(?)";

        // jdbcTemplate автоматично сматчить колонки (first_name, etc.) у ключі Map
        try {
            return jdbcTemplate.queryForMap(sql, clientId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null; // Або кинути виключення, якщо клієнт не знайдений
        }
    }

    // 1. Отримати список улюблених
    public List<Map<String, Object>> getFavoriteRooms(Long clientId) {
        return jdbcTemplate.queryForList("SELECT * FROM get_client_favorite_rooms(?)", clientId);
    }

    // 3. Видалити з улюблених
    public void removeFromFavorites(Long clientId, Long roomId) {
        jdbcTemplate.update("CALL sp_remove_favorite_room(?, ?)", clientId, roomId);
    }

    /** 8.3 Додати в улюблене */
    public void addFavoriteRoom(Long clientId, Long roomId) {
        jdbcTemplate.update("CALL sp_add_favorite_room(?, ?)", clientId, roomId);
    }

    public Map<String, Object> getRoomDetails(Long roomId) {
        String sql = "SELECT * FROM get_room_details_for_booking(?)";

        try {
            // queryForMap повертає один рядок як Map (ключі = назви колонок з SQL функції)
            return jdbcTemplate.queryForMap(sql, roomId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            // Якщо кімнату не знайдено (наприклад, ID неправильний)
            throw new RuntimeException("Room not found with ID: " + roomId);
        }
    }

    public Long bookRoom(Long clientId, Long roomId, LocalDate checkIn, LocalDate checkOut, Integer guests, String promoCode) {
        return jdbcTemplate.execute((org.springframework.jdbc.core.ConnectionCallback<Long>) con -> {
            // Викликаємо процедуру з 7 параметрами (6 вхідних + 1 вихідний)
            java.sql.CallableStatement cs = con.prepareCall("CALL sp_client_book_room(?, ?, ?, ?, ?, ?, ?)");

            cs.setLong(1, clientId);
            cs.setLong(2, roomId);
            cs.setDate(3, java.sql.Date.valueOf(checkIn));
            cs.setDate(4, java.sql.Date.valueOf(checkOut));
            cs.setInt(5, guests);

            // Обробка промокоду (може бути null)
            if (promoCode != null && !promoCode.isEmpty()) {
                cs.setString(6, promoCode);
            } else {
                cs.setNull(6, java.sql.Types.VARCHAR);
            }

            // Реєструємо вихідний параметр (ID бронювання)
            cs.registerOutParameter(7, java.sql.Types.BIGINT);

            cs.execute();
            return cs.getLong(7);
        });
    }
}