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
public class MarketingCampaign {
    private Long id;
    private BigDecimal budget;
    private String campaignType;
    private LocalDate startDate;
    private LocalDate endDate;
}