package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BookingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // --- АДМІНІСТРАТОР ---

    /** 1.2 Створення бронювання (Адмін) */
    public Long createBookingAdmin(Long clientId, Long roomId, LocalDate checkIn, LocalDate checkOut, Integer guests, String paymentMethod) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_create_booking");
        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_client_id", clientId);
        inParams.put("p_room_id", roomId);
        inParams.put("p_check_in", Date.valueOf(checkIn));
        inParams.put("p_check_out", Date.valueOf(checkOut));
        inParams.put("p_guests_count", guests);
        inParams.put("p_payment_method", paymentMethod);
        inParams.put("p_booking_id", null);

        Map<String, Object> out = jdbcCall.execute(inParams);
        return ((Number) out.get("p_booking_id")).longValue();
    }

    /** 1.3 Скасування (Адмін) */
    public void cancelBookingAdmin(Long bookingId, String reason) {
        jdbcTemplate.update("CALL sp_cancel_booking(?, ?)", bookingId, reason);
    }

    /** 1.4 Редагування */
    public void updateBooking(Long bookingId, Long newRoomId, LocalDate newCheckIn, LocalDate newCheckOut) {
        jdbcTemplate.update("CALL sp_update_booking(?, ?, ?, ?)",
                bookingId, newRoomId, Date.valueOf(newCheckIn), Date.valueOf(newCheckOut));
    }

    /** 3.2 Деталі рахунку */
    public List<Map<String, Object>> getBillDetails(Long bookingId) {
        String sql = "SELECT * FROM get_booking_bill_details(?)";
        return jdbcTemplate.queryForList(sql, bookingId);
    }

    /** 3.3 Виселення */
    public void processCheckout(Long bookingId, String paymentMethod) {
        jdbcTemplate.update("CALL sp_process_checkout(?, ?)", bookingId, paymentMethod);
    }

    /** Список активних бронювань (View) */
    public List<Map<String, Object>> getViewReceptionBookings() {
        return jdbcTemplate.queryForList("SELECT * FROM view_reception_bookings");
    }

    // --- КЛІЄНТ ---

    /** 2.2 Бронювання (Клієнт) */
    public Long createBookingClient(Long clientId, Long roomId, LocalDate checkIn, LocalDate checkOut, Integer guests) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_client_book_room");
        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_client_id", clientId);
        inParams.put("p_room_id", roomId);
        inParams.put("p_check_in", Date.valueOf(checkIn));
        inParams.put("p_check_out", Date.valueOf(checkOut));
        inParams.put("p_guests_count", guests);
        inParams.put("p_booking_id", null);

        Map<String, Object> out = jdbcCall.execute(inParams);
        return ((Number) out.get("p_booking_id")).longValue();
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
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_client_repeat_booking");
        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_client_id", clientId);
        inParams.put("p_old_booking_id", oldBookingId);
        inParams.put("p_new_check_in", Date.valueOf(newCheckIn));
        inParams.put("p_new_check_out", Date.valueOf(newCheckOut));
        inParams.put("p_new_booking_id", null);

        Map<String, Object> out = jdbcCall.execute(inParams);
        return ((Number) out.get("p_new_booking_id")).longValue();
    }
}