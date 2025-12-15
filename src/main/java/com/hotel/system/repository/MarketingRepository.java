package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class MarketingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // --- ВЛАСНИК ---

    /** 5.1 Створення акції */
    public Long createPromotion(String code, Integer discount, LocalDate validFrom, LocalDate validTo) {
        // Явний виклик процедури: 4 вхідних параметри + 1 вихідний ID
        String sql = "CALL sp_create_promotion(?, ?, ?, ?, ?)";

        return jdbcTemplate.execute(connection -> {
            CallableStatement cs = connection.prepareCall(sql);

            // Вхідні параметри (IN)
            cs.setString(1, code);
            cs.setInt(2, discount);
            cs.setDate(3, Date.valueOf(validFrom));
            cs.setDate(4, Date.valueOf(validTo));

            // Вихідний параметр (OUT) - p_new_promo_id
            cs.registerOutParameter(5, Types.BIGINT);
            cs.setNull(5, Types.BIGINT);

            return cs;
        }, (CallableStatementCallback<Long>) cs -> {
            cs.execute();
            return cs.getLong(5); // Повертаємо ID створеної акції
        });
    }

    /** 5.2 ROI (Аналітика) */
    public List<Map<String, Object>> analyzeRoi(Long hotelId) {
        String sql = "SELECT * FROM analyze_marketing_roi(?)";
        return jdbcTemplate.queryForList(sql, hotelId);
    }

    // --- КЛІЄНТ ---

    /** 4. Використання промокоду */
    public Integer usePromocode(Long clientId, String code) {
        // Явний виклик процедури: 2 вхідних + 1 вихідний (або INOUT) відсоток знижки
        String sql = "CALL sp_use_promocode(?, ?, ?)";

        return jdbcTemplate.execute(connection -> {
            CallableStatement cs = connection.prepareCall(sql);

            // Вхідні параметри (IN)
            cs.setLong(1, clientId);
            cs.setString(2, code);

            // Вихідний параметр (OUT/INOUT) - p_discount_percent
            cs.registerOutParameter(3, Types.INTEGER);
            // Якщо параметр INOUT, ініціалізуємо його 0, хоча для OUT це не обов'язково
            cs.setInt(3, 0);

            return cs;
        }, (CallableStatementCallback<Integer>) cs -> {
            cs.execute();
            return cs.getInt(3); // Повертаємо отриманий відсоток знижки
        });
    }

    // У класі com.hotel.system.repository.MarketingRepository

    // Отримати ВСІ промокоди (для власника)
    public List<Map<String, Object>> getActivePromotions() {
        String sql = "SELECT * FROM promocode ORDER BY valid_to DESC";
        return jdbcTemplate.queryForList(sql);
    }

//
//    /** 5. Доступні акції */
//    public List<Map<String, Object>> getActivePromotions() {
//        return jdbcTemplate.queryForList("SELECT * FROM view_active_promotions_client");
//    }

    // --- МЕНЕДЖЕР ---

    /** 4. Додати витрату */
    public void addExpense(BigDecimal amount, String description, String approvedBy, Long employeeId, Long campaignId, Long hotelId) {
        jdbcTemplate.update("CALL sp_add_expense(?, ?, ?, ?, ?, ?)",
                amount, description, approvedBy, employeeId, campaignId, hotelId);
    }
}