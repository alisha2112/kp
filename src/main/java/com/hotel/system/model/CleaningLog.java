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
public class CleaningLog {
    private Long cleaningLogId;
    private LocalDate cleaningLogDate;
    private String cleaningLogStatus; // 'completed', 'pending', 'skipped'
    private String notes;
    private Long roomId;
    private Long employeeId;
}
