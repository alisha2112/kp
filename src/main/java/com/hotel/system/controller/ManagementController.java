package com.hotel.system.controller;

import com.hotel.system.service.ManagementService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/management")
@RequiredArgsConstructor
public class ManagementController {

    private final ManagementService managementService;

    private Long getHotelId(HttpSession session) {
        Object id = session.getAttribute("HOTEL_ID");
        return id != null ? ((Number) id).longValue() : null;
    }

    private Long getUserId(HttpSession session) {
        return ((Number) session.getAttribute("USER_ID")).longValue();
    }

    // ================== МЕНЕДЖЕР ==================

    @GetMapping("/reports/financial")
    public ResponseEntity<?> getFinancialReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end, HttpSession session) {
        return ResponseEntity.ok(managementService.getFinancialReport(start, end, getHotelId(session)));
    }

    @GetMapping("/reports/occupancy")
    public ResponseEntity<?> getOccupancyReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end, HttpSession session) {
        return ResponseEntity.ok(managementService.getOccupancyReport(start, end, getHotelId(session)));
    }

    @GetMapping("/reports/customer-service")
    public ResponseEntity<?> getCustomerServiceReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end, HttpSession session) {
        return ResponseEntity.ok(managementService.getCustomerServiceReport(start, end, getHotelId(session)));
    }

    // 2. Персонал
    // Отримання списку персоналу
    @GetMapping("/staff")
    public ResponseEntity<?> getStaffList(HttpSession session) {
        return ResponseEntity.ok(managementService.getEmployees(getHotelId(session)));
    }

    // 2. Персонал - НАЙМ (ОНОВЛЕНО)
    @PostMapping("/staff")
    public ResponseEntity<?> hireEmployee(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false, defaultValue = "") String middleName, // <-- Додано
            @RequestParam String phone,
            @RequestParam String position,
            HttpSession session) {

        // 1. Отримуємо ID готелю з сесії (це гарантує, що менеджер наймає тільки в свій готель)
        Long hotelId = getHotelId(session);

        // 2. Викликаємо сервіс (який викличе процедуру sp_add_employee)
        Long id = managementService.hireEmployee(firstName, middleName, lastName, phone, position, hotelId);

        return ResponseEntity.ok(Map.of("message", "Employee hired successfully", "employeeId", id));
    }

    @DeleteMapping("/staff/{id}")
    public ResponseEntity<?> fireEmployee(@PathVariable Long id) {
        managementService.fireEmployee(id);
        return ResponseEntity.ok("Employee fired");
    }

    @PostMapping("/staff/{id}/shift")
    public ResponseEntity<?> assignShift(@PathVariable Long id, @RequestParam LocalDate date,
                                         @RequestParam LocalTime start, @RequestParam LocalTime end) {
        managementService.assignShift(id, date, start, end);
        return ResponseEntity.ok("Shift assigned");
    }

    // --- НОВЕ: Призначення прибирання ---
    @PostMapping("/staff/assign-cleaning")
    public ResponseEntity<?> assignCleaningTask(@RequestParam Integer roomNumber,
                                                @RequestParam Long cleanerId,
                                                @RequestParam(defaultValue = "Planned cleaning") String note) {
        managementService.assignCleaningTask(roomNumber, cleanerId, note);
        return ResponseEntity.ok("Cleaning task assigned successfully");
    }

    @GetMapping("/staff/workload")
    public ResponseEntity<?> getWorkload(@RequestParam LocalDate start, @RequestParam LocalDate end, HttpSession session) {
        return ResponseEntity.ok(managementService.analyzeStaffWorkload(start, end, getHotelId(session)));
    }

    @GetMapping("/staff/performance")
    public ResponseEntity<?> getPerformance(@RequestParam LocalDate start, @RequestParam LocalDate end, HttpSession session) {
        return ResponseEntity.ok(managementService.getStaffPerformanceAnalytics(start, end, getHotelId(session)));
    }

    @GetMapping("/staff/quality")
    public ResponseEntity<?> getQualityControl() {
        return ResponseEntity.ok(managementService.getQualityControl());
    }

    // 4. Витрати
    @PostMapping("/expenses")
    public ResponseEntity<?> addExpense(@RequestParam BigDecimal amount, @RequestParam String description,
                                        @RequestParam String approvedBy, @RequestParam Long employeeId,
                                        @RequestParam(required = false) Long campaignId) {
        managementService.addExpense(amount, description, approvedBy, employeeId, campaignId);
        return ResponseEntity.ok("Expense added");
    }

    // ================== ВЛАСНИК ==================

    @GetMapping("/owner/profit-loss")
    public ResponseEntity<?> getProfitLoss(@RequestParam LocalDate date, @RequestParam Long hotelId) {
        return ResponseEntity.ok(managementService.getDailyProfitLoss(date, hotelId));
    }

    @GetMapping("/owner/financial-detailed")
    public ResponseEntity<?> getDetailedFinance(@RequestParam Long hotelId, @RequestParam LocalDate start, @RequestParam LocalDate end) {
        return ResponseEntity.ok(managementService.getDetailedFinancialReport(hotelId, start, end));
    }

    @GetMapping("/owner/service-popularity")
    public ResponseEntity<?> getServicePopularity(@RequestParam Long hotelId, @RequestParam LocalDate start, @RequestParam LocalDate end,
                                                  @RequestParam(required = false) Long serviceId) {
        return ResponseEntity.ok(managementService.getServicePopularity(start, end, hotelId, serviceId));
    }

    @GetMapping("/owner/employee-stats")
    public ResponseEntity<?> getEmployeeStats(@RequestParam Long employeeId, @RequestParam Long hotelId) {
        return ResponseEntity.ok(managementService.getEmployeeStats(employeeId, hotelId));
    }

    @GetMapping("/owner/schedule")
    public ResponseEntity<?> viewSchedule(@RequestParam Long hotelId, @RequestParam LocalDate date) {
        return ResponseEntity.ok(managementService.viewStaffSchedule(hotelId, date, null));
    }

    @GetMapping("/owner/payroll")
    public ResponseEntity<?> getPayroll(@RequestParam Long hotelId) {
        return ResponseEntity.ok(managementService.getHotelPayroll(hotelId));
    }

    @GetMapping("/owner/occupancy-category")
    public ResponseEntity<?> analyzeOccupancy(@RequestParam Long hotelId, @RequestParam LocalDate start, @RequestParam LocalDate end) {
        return ResponseEntity.ok(managementService.analyzeOccupancy(hotelId, start, end));
    }

    @GetMapping("/owner/cancellations")
    public ResponseEntity<?> monitorCancellations(@RequestParam Long hotelId, @RequestParam LocalDate start, @RequestParam LocalDate end) {
        return ResponseEntity.ok(managementService.monitorCancellations(hotelId, start, end));
    }

    @GetMapping("/owner/sentiment")
    public ResponseEntity<?> analyzeSentiment(@RequestParam Long hotelId) {
        return ResponseEntity.ok(managementService.analyzeSentiment(hotelId, null));
    }

    @PostMapping("/owner/promotions")
    public ResponseEntity<?> createPromotion(@RequestParam String code, @RequestParam Integer discount,
                                             @RequestParam LocalDate from, @RequestParam LocalDate to) {
        Long id = managementService.createPromotion(code, discount, from, to);
        return ResponseEntity.ok(Map.of("promoId", id));
    }

    @GetMapping("/owner/roi")
    public ResponseEntity<?> analyzeRoi(@RequestParam Long hotelId) {
        return ResponseEntity.ok(managementService.analyzeMarketingRoi(hotelId));
    }

    // ================== БУХГАЛТЕР ==================

    @PostMapping("/accountant/payroll")
    public ResponseEntity<?> processPayroll(@RequestParam Long employeeId, @RequestParam LocalDate start,
                                            @RequestParam LocalDate end, @RequestParam(defaultValue = "0") BigDecimal bonuses,
                                            @RequestParam(defaultValue = "0") BigDecimal penalties) {
        managementService.processPayroll(employeeId, start, end, bonuses, penalties);
        return ResponseEntity.ok("Payroll processed");
    }

    @GetMapping("/accountant/profitability")
    public ResponseEntity<?> analyzeProfitability(@RequestParam Long hotelId, @RequestParam LocalDate start, @RequestParam LocalDate end) {
        return ResponseEntity.ok(managementService.analyzeHotelProfitability(hotelId, start, end));
    }

    @GetMapping("/accountant/tax")
    public ResponseEntity<?> calculateTax(@RequestParam Long hotelId, @RequestParam LocalDate start,
                                          @RequestParam LocalDate end, @RequestParam BigDecimal rate) {
        return ResponseEntity.ok(Map.of("taxAmount", managementService.calculateTax(hotelId, start, end, rate)));
    }

    // ================== ПРИБИРАЛЬНИЦЯ ==================

    @GetMapping("/cleaner/schedule")
    public ResponseEntity<?> getCleaningSchedule(HttpSession session) {
        return ResponseEntity.ok(managementService.getCleaningSchedule(getHotelId(session), LocalDate.now(), getUserId(session)));
    }

    @PostMapping("/cleaner/issue")
    public ResponseEntity<?> reportIssue(@RequestParam Integer roomNumber, @RequestParam String description, HttpSession session) {
        managementService.reportIssue(getHotelId(session), roomNumber, description, getUserId(session));
        return ResponseEntity.ok("Issue reported");
    }

    @PostMapping("/cleaner/mark-cleaned")
    public ResponseEntity<?> markCleaned(@RequestParam Integer roomNumber, @RequestParam String notes, HttpSession session) {
        managementService.markRoomCleaned(getHotelId(session), roomNumber, getUserId(session), LocalDate.now(), notes);
        return ResponseEntity.ok("Marked as cleaned");
    }
}