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
public class Service {
    private Long serviceId;
    private String serviceName;
    private BigDecimal servicePrice;
    private String serviceDescription;
    private Long hotelId;
}
