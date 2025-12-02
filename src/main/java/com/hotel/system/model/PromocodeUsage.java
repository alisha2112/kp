package com.hotel.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromocodeUsage {
    private Long promocodeUsageId;
    private LocalDate usageDate;
    private Long clientId;
    private Long promocodeId;
}
