package com.hotel.system.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSearchResult {
    private String hotelName;
    private Integer stars;
    private Integer roomNumber;
    private String comfortLevel;
    private Integer capacity;
    private BigDecimal pricePerNight;
    private BigDecimal totalPrice;
    private Long roomId;
}