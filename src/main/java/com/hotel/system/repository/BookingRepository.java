package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class BookingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // --- АДМІНІСТРАТОР ---

    /** 1.2 Створення бронювання (Адмін) */
    public Long createBookingAdmin(Long clientId, Long roomId, LocalDate checkIn, LocalDate checkOut, Integer guests, String paymentMethod) {
        // Явний виклик процедури з OUT параметром
        String sql = "CALL sp_create_booking(?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.execute(connection -> {
            CallableStatement cs = connection.prepareCall(sql);

            // Вхідні параметри (IN)
            cs.setLong(1, clientId);
            cs.setLong(2, roomId);
            cs.setDate(3, Date.valueOf(checkIn));
            cs.setDate(4, Date.valueOf(checkOut));
            cs.setInt(5, guests);
            cs.setString(6, paymentMethod);

            // Вихідний параметр (OUT) - p_booking_id
            cs.registerOutParameter(7, Types.BIGINT);
            cs.setNull(7, Types.BIGINT);

            return cs;
        }, (CallableStatementCallback<Long>) cs -> {
            cs.execute();
            return cs.getLong(7); // Повертаємо ID створеного бронювання
        });
    }

    /** 1.3 Скасування (Адмін) */
    public void cancelBookingAdmin(Long bookingId, String reason) {
        // Для void процедур достатньо jdbcTemplate.update з синтаксисом CALL
        jdbcTemplate.update("CALL sp_cancel_booking(?, ?)", bookingId, reason);
    }

    /** 1.4 Редагування */
    public void updateBooking(Long bookingId, Long newRoomId, LocalDate newCheckIn, LocalDate newCheckOut) {
        jdbcTemplate.update("CALL sp_update_booking(?, ?, ?, ?)",
                bookingId, newRoomId, Date.valueOf(newCheckIn), Date.valueOf(newCheckOut));
    }

    /** 3.2 Деталі рахунку (ОНОВЛЕНО: з hotelId) */
    public List<Map<String, Object>> getBillDetails(Long bookingId, Long hotelId) {
        // Викликаємо оновлену функцію з двома параметрами
        String sql = "SELECT * FROM get_booking_bill_details(?, ?)";
        return jdbcTemplate.queryForList(sql, bookingId, hotelId);
    }

    /** 3.3 Виселення (ОНОВЛЕНО: з hotelId) */
    public void processCheckout(Long bookingId, String paymentMethod, Long hotelId) {
        // Викликаємо оновлену процедуру з 3 параметрами
        jdbcTemplate.update("CALL sp_process_checkout(?, ?, ?)", bookingId, paymentMethod, hotelId);
    }

    /** Список активних бронювань (ОНОВЛЕНО: Фільтр по готелю) */
    public List<Map<String, Object>> getViewReceptionBookings(Long hotelId) {
        // Викликаємо нашу нову функцію замість простого SELECT з View
        String sql = "SELECT * FROM get_admin_bookings(?)";
        return jdbcTemplate.queryForList(sql, hotelId);
    }

    // --- КЛІЄНТ ---

    /** 2.2 Бронювання (Клієнт) */
    public Long createBookingClient(Long clientId, Long roomId, LocalDate checkIn, LocalDate checkOut, Integer guests) {
        // Явний виклик процедури з OUT параметром
        String sql = "CALL sp_client_book_room(?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.execute(connection -> {
            CallableStatement cs = connection.prepareCall(sql);

            // Вхідні параметри (IN)
            cs.setLong(1, clientId);
            cs.setLong(2, roomId);
            cs.setDate(3, Date.valueOf(checkIn));
            cs.setDate(4, Date.valueOf(checkOut));
            cs.setInt(5, guests);

            // Вихідний параметр (OUT) - p_booking_id
            cs.registerOutParameter(6, Types.BIGINT);
            cs.setNull(6, Types.BIGINT);

            return cs;
        }, (CallableStatementCallback<Long>) cs -> {
            cs.execute();
            return cs.getLong(6); // Повертаємо ID створеного бронювання
        });
    }

    /** 2.3 Скасування (Клієнт) */
    public void cancelBookingClient(Long clientId, Long bookingId, String reason) {
        jdbcTemplate.update("CALL sp_client_cancel_booking(?, ?, ?)", clientId, bookingId, reason);
    }

    /** 8.1 Історія проживання */
    public List<Map<String, Object>> getClientHistory(Long clientId) {
        String sql = "SELECT * FROM get_client_stay_history(?)";
        return jdbcTemplate.queryForList(sql, clientId);
    }

    /** 8.2 Повторне бронювання */
    public Long repeatBooking(Long clientId, Long oldBookingId, LocalDate newCheckIn, LocalDate newCheckOut) {
        // Явний виклик процедури з OUT параметром
        String sql = "CALL sp_client_repeat_booking(?, ?, ?, ?, ?)";

        return jdbcTemplate.execute(connection -> {
            CallableStatement cs = connection.prepareCall(sql);

            // Вхідні параметри (IN)
            cs.setLong(1, clientId);
            cs.setLong(2, oldBookingId);
            cs.setDate(3, Date.valueOf(newCheckIn));
            cs.setDate(4, Date.valueOf(newCheckOut));

            // Вихідний параметр (OUT) - p_new_booking_id
            cs.registerOutParameter(5, Types.BIGINT);
            cs.setNull(5, Types.BIGINT);

            return cs;
        }, (CallableStatementCallback<Long>) cs -> {
            cs.execute();
            return cs.getLong(5); // Повертаємо ID нового бронювання
        });
    }
}