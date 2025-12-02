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
public class RoomIssue {
    private Long issueId;
    private LocalDate reportedAt;
    private String reportedBy;
    private String description;
    private String status; // 'open', 'resolved', 'in_progress'
    private Long roomId;
}
