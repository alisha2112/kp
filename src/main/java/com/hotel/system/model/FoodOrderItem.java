package com.hotel.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodOrderItem {
    private Long foodOrderItemId;
    private Integer quantity;
    private Long foodOrderId;
    private Long menuItemId;
}
