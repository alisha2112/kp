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
public class FoodOrder {
    private Long foodOrderId;
    private LocalDate orderDate;
    private String status;
    private Long clientId;
    private Long roomId;
}