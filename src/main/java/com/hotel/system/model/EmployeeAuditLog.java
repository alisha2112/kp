package com.hotel.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAuditLog {
    private Long logId;
    private Long employeeId;
    private String fullName;
    private String position;
    private LocalDateTime deletedAt;
    private String deletedBy;
}