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
public class FavouriteRoom {
    private Long favouriteRoomId;
    private LocalDate createdAt;
    private Long roomId;
    private Long clientId;
}