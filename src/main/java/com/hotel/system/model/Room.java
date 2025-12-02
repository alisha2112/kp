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
public class Room {
    private Long roomId;
    private Integer roomNumber;
    private BigDecimal pricePerNight; // NUMERIC -> BigDecimal
    private Integer capacity;
    private String comfortLevel;      // 'luxury', 'standard' тощо
    private String status;            // 'available', 'occupied'
    private Long hotelId;
}