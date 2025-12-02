package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** 6.1 Запит на послугу */
    public void requestService(Long clientId, Long bookingId, Long serviceId) {
        jdbcTemplate.update("CALL sp_client_request_service(?, ?, ?)", clientId, bookingId, serviceId);
    }

    /** 6.2 Замовлення їжі (JSON) */
    public void orderFood(Long clientId, Long roomId, String itemsJson) {
        // itemsJson має бути формату '[{"id": 1, "qty": 2}]'
        jdbcTemplate.update("CALL sp_client_order_food(?, ?, ?::json)", clientId, roomId, itemsJson);
    }
}