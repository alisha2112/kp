package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DictionaryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** Отримати список страв (Меню) */
    public List<Map<String, Object>> getMenu() {
        return jdbcTemplate.queryForList("SELECT * FROM menu_items WHERE is_available = TRUE");
    }

    /** Отримати список додаткових послуг готелю */
    public List<Map<String, Object>> getServices() {
        return jdbcTemplate.queryForList("SELECT * FROM services");
    }

    /** Отримати список доступних статусів (для фільтрів) */
    public List<Map<String, Object>> getBookingStatuses() {
        // Оскільки це enum/check constraint в БД, ми можемо або хардкодити,
        // або отримати унікальні значення, які вже використовуються
        return jdbcTemplate.queryForList("SELECT DISTINCT status FROM bookings");
    }
}