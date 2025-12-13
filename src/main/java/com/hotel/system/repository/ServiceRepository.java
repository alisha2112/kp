package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ServiceRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 1. Отримати список послуг (для відображення у формі)
    public List<Map<String, Object>> getAllServices() {
        // Таблиця services описана у вашому файлі: service_id, service_name, service_price
        String sql = "SELECT service_id, service_name, service_price, service_description FROM services";
        return jdbcTemplate.queryForList(sql);
    }

    // 2. Замовити послугу (Виклик вашої процедури з файлу Бізнес-логіка)
    public void requestService(Long clientId, Long bookingId, Long serviceId) {
        jdbcTemplate.update("CALL sp_client_request_service(?, ?, ?)", clientId, bookingId, serviceId);
    }

    public List<Map<String, Object>> getMenu() {
        String sql = "SELECT * FROM menu_items WHERE is_available = TRUE ORDER BY name";
        return jdbcTemplate.queryForList(sql);
    }

    // 2. Отримати список АКТИВНИХ кімнат клієнта (щоб знати room_id для процедури)
    public List<Map<String, Object>> getActiveRooms(Long clientId) {
        String sql = """
            SELECT b.booking_id, r.room_id, r.room_number, h.name as hotel_name 
            FROM bookings b
            JOIN rooms r ON b.room_id = r.room_id
            JOIN hotels h ON r.hotel_id = h.hotel_id
            WHERE b.client_id = ? 
              AND b.status = 'confirmed' 
              -- Перевіряємо, чи клієнт проживає зараз (вимога процедури sp_client_order_food)
              AND CURRENT_DATE BETWEEN b.check_in AND b.check_out
        """;
        return jdbcTemplate.queryForList(sql, clientId);
    }

    /** 6.2 Замовлення їжі (JSON) */
    public void orderFood(Long clientId, Long roomId, String itemsJson) {
        // itemsJson має бути формату '[{"id": 1, "qty": 2}]'
        jdbcTemplate.update("CALL sp_client_order_food(?, ?, ?::json)", clientId, roomId, itemsJson);
    }
}