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
public class Booking {
    private Long bookingId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guestsCount;
    private String paymentMethod; // 'cash', 'credit-card'
    private String status;        // 'confirmed', 'cancelled', 'completed'
    private String cancelReason;

    private Long hotelId;
    private Long clientId;
    private Long roomId;
}