package com.hotel.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {
    private Long hotelId;       // BIGSERIAL -> Long
    private String name;
    private String street;
    private String buildingNumber;
    private String city;
    private String description;
    private String policy;
    private Integer stars;
}