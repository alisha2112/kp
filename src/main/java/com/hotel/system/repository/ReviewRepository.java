package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ReviewRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getBookingsForReview(Long clientId) {
        return jdbcTemplate.queryForList("SELECT * FROM get_client_bookings_to_review(?)", clientId);
    }

    public void leaveReview(Long clientId, Long bookingId, Integer rating, String comment) {
        jdbcTemplate.update("CALL sp_client_leave_review(?, ?, ?, ?)", clientId, bookingId, rating, comment);
    }

    /** 3.4 (Адмін) / 7 (Клієнт) Створення відгуку */
    public void addReview(Long bookingId, Integer rating, String comment, boolean isClient) {
        String procName = isClient ? "sp_client_leave_review" : "sp_add_review";

        // Параметри трохи відрізняються: клієнт передає свій ID, адмін - ні (бо він це робить від імені системи)
        // Для спрощення припустимо, що у нас є універсальний метод, або розбиваємо логіку в сервісі.
        // Тут приклад для адміна:
        if (!isClient) {
            jdbcTemplate.update("CALL sp_add_review(?, ?, ?)", bookingId, rating, comment);
        }
    }

    public void addClientReview(Long clientId, Long bookingId, Integer rating, String comment) {
        jdbcTemplate.update("CALL sp_client_leave_review(?, ?, ?, ?)", clientId, bookingId, rating, comment);
    }

    /** 4. Перевірка відгуків (Гість) */
    public List<Map<String, Object>> getPublicReviews(String hotelName) {
        String sql = "SELECT * FROM get_hotel_reviews_public(?)";
        return jdbcTemplate.queryForList(sql, hotelName);
    }

    /** 4. Аналіз настроїв (Власник) */
    public List<Map<String, Object>> analyzeSentiment(Long hotelId, Long employeeId) {
        String sql = "SELECT * FROM analyze_reviews_sentiment(?, ?)";
        return jdbcTemplate.queryForList(sql, hotelId, employeeId);
    }
}