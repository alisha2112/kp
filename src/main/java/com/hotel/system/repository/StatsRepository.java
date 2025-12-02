package com.hotel.system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class StatsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // --- МЕНЕДЖЕР ---

    /** 1.1 Звітність фінансових операцій */
    public Map<String, Object> getManagerFinancialReport(LocalDate start, LocalDate end, Long hotelId) {
        String sql = "SELECT * FROM get_manager_financial_report(?, ?, ?)";
        return jdbcTemplate.queryForMap(sql, Date.valueOf(start), Date.valueOf(end), hotelId);
    }

    /** 1.2 Звіт про зайнятість номерів */
    public Map<String, Object> getOccupancyReport(LocalDate start, LocalDate end, Long hotelId) {
        String sql = "SELECT * FROM get_occupancy_report(?, ?, ?)";
        return jdbcTemplate.queryForMap(sql, Date.valueOf(start), Date.valueOf(end), hotelId);
    }

    /** 1.3 Звіт про клієнтське обслуговування */
    public Map<String, Object> getCustomerServiceReport(LocalDate start, LocalDate end, Long hotelId) {
        String sql = "SELECT * FROM get_customer_service_report(?, ?, ?)";
        return jdbcTemplate.queryForMap(sql, Date.valueOf(start), Date.valueOf(end), hotelId);
    }

    /** 1.4 Аналіз ефективності роботи персоналу */
    public List<Map<String, Object>> getStaffPerformanceAnalytics(LocalDate start, LocalDate end, Long hotelId) {
        String sql = "SELECT * FROM get_staff_performance_analytics(?, ?, ?)";
        return jdbcTemplate.queryForList(sql, Date.valueOf(start), Date.valueOf(end), hotelId);
    }

    /** 2.4 Контроль завантаженості персоналу */
    public Map<String, Object> getStaffWorkloadAnalysis(LocalDate start, LocalDate end, Long hotelId) {
        String sql = "SELECT * FROM get_staff_workload_analysis(?, ?, ?)";
        return jdbcTemplate.queryForMap(sql, Date.valueOf(start), Date.valueOf(end), hotelId);
    }

    // --- ВЛАСНИК ---

    /** 1.1 Контроль прибутку та витрат (щоденний) */
    public Map<String, Object> getDailyProfitLoss(LocalDate date, Long hotelId) {
        String sql = "SELECT * FROM get_daily_profit_loss(?, ?)";
        return jdbcTemplate.queryForMap(sql, Date.valueOf(date), hotelId);
    }

    /** 1.2 Формування детальних фінансових звітів */
    public List<Map<String, Object>> getDetailedFinancialReport(Long hotelId, LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM get_detailed_financial_report(?, ?, ?)";
        return jdbcTemplate.queryForList(sql, hotelId, Date.valueOf(start), Date.valueOf(end));
    }

    /** 1.3 Аналіз популярності послуг */
    public List<Map<String, Object>> getServicePopularity(LocalDate start, LocalDate end, Long hotelId, Long serviceId) {
        String sql = "SELECT * FROM analyze_service_popularity(?, ?, ?, ?)";
        return jdbcTemplate.queryForList(sql, Date.valueOf(start), Date.valueOf(end), hotelId, serviceId);
    }

    /** 2.1 Контроль продуктивності (Власник) */
    public Map<String, Object> getOwnerEmployeeStats(Long employeeId, Long hotelId) {
        String sql = "SELECT * FROM get_owner_employee_stats(?, ?)";
        return jdbcTemplate.queryForMap(sql, employeeId, hotelId);
    }

    /** 3.1 Аналіз заповнюваності по категоріях */
    public List<Map<String, Object>> analyzeOccupancyByCategory(Long hotelId, LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM analyze_occupancy_by_category(?, ?, ?)";
        return jdbcTemplate.queryForList(sql, hotelId, Date.valueOf(start), Date.valueOf(end));
    }

    /** 3.2 Моніторинг відмов */
    public List<Map<String, Object>> monitorCancellations(Long hotelId, LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM monitor_cancellations(?, ?, ?)";
        return jdbcTemplate.queryForList(sql, hotelId, Date.valueOf(start), Date.valueOf(end));
    }

    // --- ФІНАНСОВИЙ ВІДДІЛ ---

    /** 2. Аналіз прибутковості готелів */
    public Map<String, Object> analyzeHotelProfitability(Long hotelId, LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM analyze_hotel_profitability_period(?, ?, ?)";
        return jdbcTemplate.queryForMap(sql, hotelId, Date.valueOf(start), Date.valueOf(end));
    }

    /** 3. Податковий облік */
    public BigDecimal calculateTaxLiability(Long hotelId, LocalDate start, LocalDate end, BigDecimal taxRate) {
        String sql = "SELECT calculate_tax_liability(?, ?, ?, ?)";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, hotelId, Date.valueOf(start), Date.valueOf(end), taxRate);
    }
}
