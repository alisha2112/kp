package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MarketingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // --- ВЛАСНИК ---

    /** 5.1 Створення акції */
    public Long createPromotion(String code, Integer discount, LocalDate validFrom, LocalDate validTo) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_create_promotion");
        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_code", code);
        inParams.put("p_discount_percent", discount);
        inParams.put("p_valid_from", Date.valueOf(validFrom));
        inParams.put("p_valid_to", Date.valueOf(validTo));
        inParams.put("p_new_promo_id", null);

        Map<String, Object> out = jdbcCall.execute(inParams);
        return ((Number) out.get("p_new_promo_id")).longValue();
    }

    /** 5.2 ROI (Аналітика) */
    public List<Map<String, Object>> analyzeRoi(Long hotelId) {
        String sql = "SELECT * FROM analyze_marketing_roi(?)";
        return jdbcTemplate.queryForList(sql, hotelId);
    }

    // --- КЛІЄНТ ---

    /** 4. Використання промокоду */
    public Integer usePromocode(Long clientId, String code) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_use_promocode");
        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_client_id", clientId);
        inParams.put("p_code_string", code);
        inParams.put("p_discount_percent", 0); // INOUT

        Map<String, Object> out = jdbcCall.execute(inParams);
        return ((Number) out.get("p_discount_percent")).intValue();
    }

    /** 5. Доступні акції */
    public List<Map<String, Object>> getActivePromotions() {
        return jdbcTemplate.queryForList("SELECT * FROM view_active_promotions_client");
    }

    // --- МЕНЕДЖЕР ---

    /** 4. Додати витрату */
    public void addExpense(BigDecimal amount, String description, String approvedBy, Long employeeId, Long campaignId) {
        jdbcTemplate.update("CALL sp_add_expense(?, ?, ?, ?, ?)",
                amount, description, approvedBy, employeeId, campaignId);
    }
}