package com.hotel.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollHistory {
    private Long payrollId;
    private Long employeeId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal baseSalarySnapshot;
    private BigDecimal bonuses;
    private BigDecimal penalties;
    private BigDecimal taxesDeducted;
    private BigDecimal netPayout;
    private LocalDate paymentDate;
}