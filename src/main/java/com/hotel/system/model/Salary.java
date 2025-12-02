package com.hotel.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Salary {
    private Long salaryId;
    private BigDecimal baseSalary;
    private BigDecimal bonuses;
    private BigDecimal penalties;
    private BigDecimal tax;
    private Long employeeId;
}
