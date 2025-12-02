package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class HotelRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** 1. Перегляд каталогу */
    public List<Map<String, Object>> getPublicCatalog() {
        return jdbcTemplate.queryForList("SELECT * FROM view_public_hotel_catalog");
    }
}