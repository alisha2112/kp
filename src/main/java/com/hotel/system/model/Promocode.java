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
public class Promocode {
    private Long promocodeId;
    private String code;
    private Integer discountPercent;
    private LocalDate validFrom;
    private LocalDate validTo;
}
