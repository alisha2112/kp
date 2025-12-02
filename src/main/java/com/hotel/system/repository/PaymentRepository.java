package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class PaymentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // --- АДМІНІСТРАТОР ---

    /** 4.1 Прийом оплати */
    public void acceptPaymentAdmin(Long bookingId, String lastName, String firstName, String middleName, String method) {
        jdbcTemplate.update("CALL sp_accept_payment_secure(?, ?, ?, ?, ?)",
                bookingId, lastName, firstName, middleName, method);
    }

    /** 4.2 Контроль боргів */
    public Map<String, Object> getDebtStatus(Long bookingId) {
        String sql = "SELECT * FROM get_booking_debt_status(?)";
        return jdbcTemplate.queryForMap(sql, bookingId);
    }

    // --- КЛІЄНТ ---

    /** 3.1 Оплата онлайн */
    public void payOnlineClient(Long clientId, Long bookingId, String cardToken) {
        jdbcTemplate.update("CALL sp_client_pay_online(?, ?, ?)", clientId, bookingId, cardToken);
    }

    /** 3.2 Історія оплат */
    public List<Map<String, Object>> getClientPaymentHistory(Long clientId) {
        String sql = "SELECT * FROM get_client_payment_history(?)";
        return jdbcTemplate.queryForList(sql, clientId);
    }
}