package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class RoomRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** 1.1 Перевірка наявності (Адмін) - ОНОВЛЕНО */
    public List<Map<String, Object>> getAvailableRoomsAdmin(LocalDate checkIn, LocalDate checkOut, Long hotelId) {
        // Додали третій параметр у SQL виклик
        String sql = "SELECT * FROM get_available_rooms(?, ?, ?)";
        return jdbcTemplate.queryForList(sql, Date.valueOf(checkIn), Date.valueOf(checkOut), hotelId);
    }

    /** 3.1 Розширений статус (Адмін) */
    public Map<String, Object> getRoomExtendedStatus(Integer roomNumber) {
        String sql = "SELECT * FROM get_room_extended_status(?)";
        return jdbcTemplate.queryForMap(sql, roomNumber);
    }

    /** 2 (Гість) / 2.1 (Клієнт): Публічний пошук */
    public List<Map<String, Object>> searchRoomsPublic(String city, LocalDate checkIn, LocalDate checkOut, Integer guests, String comfortLevel) {
        String sql = "SELECT * FROM search_available_rooms_public(?, ?, ?, ?, ?)";
        return jdbcTemplate.queryForList(sql, city, Date.valueOf(checkIn), Date.valueOf(checkOut), guests, comfortLevel);
    }

    /** 2.1 (Клієнт): Пошук для зареєстрованих */
    public List<Map<String, Object>> getRoomsForClient(LocalDate checkIn, LocalDate checkOut, String city) {
        String sql = "SELECT * FROM get_available_rooms_for_client(?, ?, ?)";
        return jdbcTemplate.queryForList(sql, Date.valueOf(checkIn), Date.valueOf(checkOut), city);
    }

    // --- ПРИБИРАЛЬНИЦЯ ---

    /** 2. Повідомлення про проблему */
    public void reportIssue(Long hotelId, Integer roomNumber, String description, Long cleanerId) {
        jdbcTemplate.update("CALL sp_report_issue_by_cleaner(?, ?, ?, ?)",
                hotelId, roomNumber, description, cleanerId);
    }

    /** 3. Відмітка про прибирання */
    public void markRoomCleaned(Long hotelId, Integer roomNumber, Long cleanerId, LocalDate date, String notes) {
        jdbcTemplate.update("CALL sp_mark_room_cleaned(?, ?, ?, ?, ?)",
                hotelId, roomNumber, cleanerId, Date.valueOf(date), notes);
    }
}