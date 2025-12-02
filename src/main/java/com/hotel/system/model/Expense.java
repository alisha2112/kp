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
public class Expense {
    private Long expenseId;
    private BigDecimal amount;
    private String approvedBy;
    private LocalDate date;
    private String description;
    private String status;
    private Long employeeId;
    private Long marketingCampaignId;
}