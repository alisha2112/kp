package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Repository
public class EmployeeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // --- УПРАВЛІННЯ ПЕРСОНАЛОМ (Менеджер) ---

    /** 2.1 Додавання співробітника */
    public Long addEmployee(String firstName, String middleName, String lastName, String phone, String position, Long hotelId) {
        // Явний виклик процедури для PostgreSQL
        // 6 вхідних параметрів + 1 вихідний = 7 знаків питання
        String sql = "CALL sp_add_employee(?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.execute(connection -> {
            CallableStatement cs = connection.prepareCall(sql);

            // Вхідні параметри (IN)
            cs.setString(1, firstName);
            cs.setString(2, middleName);
            cs.setString(3, lastName);
            cs.setString(4, phone);
            cs.setString(5, position);
            cs.setLong(6, hotelId);

            // Вихідний параметр (OUT) - p_new_emp_id
            cs.registerOutParameter(7, Types.BIGINT);
            cs.setNull(7, Types.BIGINT);

            return cs;
        }, (CallableStatementCallback<Long>) cs -> {
            cs.execute();
            return cs.getLong(7); // Повертаємо ID нового співробітника
        });
    }

    /** 2.2 Видалення співробітника */
    public void fireEmployee(Long employeeId) {
        jdbcTemplate.update("CALL sp_fire_employee(?)", employeeId);
    }

    /** 2.3 Розподіл змін */
    public void assignShift(Long employeeId, LocalDate date, LocalTime start, LocalTime end) {
        jdbcTemplate.update("CALL sp_assign_shift(?, ?, ?, ?)",
                employeeId, Date.valueOf(date), Time.valueOf(start), Time.valueOf(end));
    }

    /** 2.5 Контроль якості (View) */
    public List<Map<String, Object>> getEmployeeQualityControl() {
        return jdbcTemplate.queryForList("SELECT * FROM view_employee_quality_control");
    }

    // --- ВЛАСНИК ---

    /** 2.2 Перегляд розкладу */
    public List<Map<String, Object>> viewSchedule(Long hotelId, LocalDate date, Long employeeId) {
        String sql = "SELECT * FROM view_staff_schedule_owner(?, ?, ?)";
        return jdbcTemplate.queryForList(sql, hotelId, Date.valueOf(date), employeeId);
    }

    /** 2.3 Зарплатна відомість готелю */
    public List<Map<String, Object>> getHotelPayroll(Long hotelId) {
        String sql = "SELECT * FROM get_hotel_payroll(?)";
        return jdbcTemplate.queryForList(sql, hotelId);
    }

    // --- ФІНАНСОВИЙ ВІДДІЛ ---

    /** 1. Нарахування ЗП */
    public void processPayroll(Long employeeId, LocalDate start, LocalDate end, BigDecimal bonuses, BigDecimal penalties) {
        jdbcTemplate.update("CALL sp_process_payroll(?, ?, ?, ?, ?)",
                employeeId, Date.valueOf(start), Date.valueOf(end), bonuses, penalties);
    }

    // --- ПРИБИРАЛЬНИЦЯ ---

    /** 1. Графік прибирань */
    public List<Map<String, Object>> getCleanerSchedule(Long hotelId, LocalDate date, Long cleanerId) {
        String sql = "SELECT * FROM get_cleaner_schedule(?, ?, ?)";
        return jdbcTemplate.queryForList(sql, hotelId, Date.valueOf(date), cleanerId);
    }

    /** Призначення завдання на прибирання (Менеджер) */
    public void assignCleaningTask(Integer roomNumber, Long cleanerId, String note) {
        jdbcTemplate.update("CALL sp_assign_cleaning_task(?, ?, ?)", roomNumber, cleanerId, note);
    }
}