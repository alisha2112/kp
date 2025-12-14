package com.hotel.system.service;

import com.hotel.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ManagementService {

    private final StatsRepository statsRepository;
    private final EmployeeRepository employeeRepository;
    private final MarketingRepository marketingRepository;
    private final RoomRepository roomRepository;
    private final ReviewRepository reviewRepository;

    // ==========================================
    // РОЛЬ: МЕНЕДЖЕР (MANAGER)
    // ==========================================

    // 1. Звітність
    public Map<String, Object> getFinancialReport(LocalDate start, LocalDate end, Long hotelId) {
        return statsRepository.getManagerFinancialReport(start, end, hotelId);
    }

    public Map<String, Object> getOccupancyReport(LocalDate start, LocalDate end, Long hotelId) {
        return statsRepository.getOccupancyReport(start, end, hotelId);
    }

    public Map<String, Object> getCustomerServiceReport(LocalDate start, LocalDate end, Long hotelId) {
        return statsRepository.getCustomerServiceReport(start, end, hotelId);
    }

    public List<Map<String, Object>> getStaffPerformanceAnalytics(LocalDate start, LocalDate end, Long hotelId) {
        return statsRepository.getStaffPerformanceAnalytics(start, end, hotelId);
    }

    // Отримати список працівників
    public List<Map<String, Object>> getEmployees(Long hotelId) {
        return employeeRepository.getEmployeesByHotel(hotelId);
    }

    // 2. Управління персоналом
    @Transactional
    public Long hireEmployee(String firstName, String middleName, String lastName, String phone, String position, Long hotelId) {
        return employeeRepository.addEmployee(firstName, middleName, lastName, phone, position, hotelId);
    }

    @Transactional
    public void fireEmployee(Long employeeId) {
        employeeRepository.fireEmployee(employeeId);
    }

    // 2.3 Зміни - ОНОВЛЕНО
    @Transactional
    public void assignShift(Long employeeId, LocalDate date, LocalTime start, LocalTime end, Long hotelId) {
        employeeRepository.assignShift(employeeId, date, start, end, hotelId);
    }

    // Перегляд розкладу
    public List<Map<String, Object>> viewSchedule(Long hotelId, LocalDate date) {
        return employeeRepository.getHotelSchedule(hotelId, date);
    }

    // 2.5 Прибирання - ОНОВЛЕНО
    @Transactional
    public void assignCleaningTask(Integer roomNumber, Long cleanerId, String note, Long hotelId) {
        roomRepository.assignCleaningTask(roomNumber, cleanerId, note, hotelId);
    }

//    public Map<String, Object> analyzeStaffWorkload(LocalDate start, LocalDate end, Long hotelId) {
//        return statsRepository.getStaffWorkloadAnalysis(start, end, hotelId);
//    }

    // 2.6 Workload
    public List<Map<String, Object>> analyzeStaffWorkload(LocalDate start, LocalDate end, Long hotelId) {
        return employeeRepository.getStaffWorkload(start, end, hotelId);
    }

    public List<Map<String, Object>> getQualityControl() {
        return employeeRepository.getEmployeeQualityControl();
    }

    // 4. Витрати
    @Transactional
    public void addExpense(BigDecimal amount, String description, String approvedBy, Long employeeId, Long campaignId) {
        marketingRepository.addExpense(amount, description, approvedBy, employeeId, campaignId);
    }

    // ==========================================
    // РОЛЬ: ВЛАСНИК (OWNER)
    // ==========================================

    public Map<String, Object> getDailyProfitLoss(LocalDate date, Long hotelId) {
        return statsRepository.getDailyProfitLoss(date, hotelId);
    }

    public List<Map<String, Object>> getDetailedFinancialReport(Long hotelId, LocalDate start, LocalDate end) {
        return statsRepository.getDetailedFinancialReport(hotelId, start, end);
    }

    public List<Map<String, Object>> getServicePopularity(LocalDate start, LocalDate end, Long hotelId, Long serviceId) {
        return statsRepository.getServicePopularity(start, end, hotelId, serviceId);
    }

    public Map<String, Object> getEmployeeStats(Long employeeId, Long hotelId) {
        return statsRepository.getOwnerEmployeeStats(employeeId, hotelId);
    }

    public List<Map<String, Object>> viewStaffSchedule(Long hotelId, LocalDate date, Long employeeId) {
        return employeeRepository.viewSchedule(hotelId, date, employeeId);
    }

    public List<Map<String, Object>> getHotelPayroll(Long hotelId) {
        return employeeRepository.getHotelPayroll(hotelId);
    }

    public List<Map<String, Object>> analyzeOccupancy(Long hotelId, LocalDate start, LocalDate end) {
        return statsRepository.analyzeOccupancyByCategory(hotelId, start, end);
    }

    public List<Map<String, Object>> monitorCancellations(Long hotelId, LocalDate start, LocalDate end) {
        return statsRepository.monitorCancellations(hotelId, start, end);
    }

    public List<Map<String, Object>> analyzeSentiment(Long hotelId, Long employeeId) {
        return reviewRepository.analyzeSentiment(hotelId, employeeId);
    }

    // Маркетинг
    @Transactional
    public Long createPromotion(String code, Integer discount, LocalDate validFrom, LocalDate validTo) {
        return marketingRepository.createPromotion(code, discount, validFrom, validTo);
    }

    public List<Map<String, Object>> analyzeMarketingRoi(Long hotelId) {
        return marketingRepository.analyzeRoi(hotelId);
    }

    // ==========================================
    // РОЛЬ: ФІНАНСОВИЙ ВІДДІЛ (ACCOUNTANT)
    // ==========================================

    @Transactional
    public void processPayroll(Long employeeId, LocalDate start, LocalDate end, BigDecimal bonuses, BigDecimal penalties) {
        employeeRepository.processPayroll(employeeId, start, end, bonuses, penalties);
    }

    public Map<String, Object> analyzeHotelProfitability(Long hotelId, LocalDate start, LocalDate end) {
        return statsRepository.analyzeHotelProfitability(hotelId, start, end);
    }

    public BigDecimal calculateTax(Long hotelId, LocalDate start, LocalDate end, BigDecimal taxRate) {
        return statsRepository.calculateTaxLiability(hotelId, start, end, taxRate);
    }

    // ==========================================
    // РОЛЬ: ПРИБИРАЛЬНИЦЯ (CLEANER)
    // ==========================================

    public List<Map<String, Object>> getCleaningSchedule(Long hotelId, LocalDate date, Long cleanerId) {
        return employeeRepository.getCleanerSchedule(hotelId, date, cleanerId);
    }

    @Transactional
    public void reportIssue(Long hotelId, Integer roomNumber, String description, Long cleanerId) {
        roomRepository.reportIssue(hotelId, roomNumber, description, cleanerId);
    }

    @Transactional
    public void markRoomCleaned(Long hotelId, Integer roomNumber, Long cleanerId, LocalDate date, String notes) {
        roomRepository.markRoomCleaned(hotelId, roomNumber, cleanerId, date, notes);
    }
}