package com.securetask.DTO.responses;

import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    String title,
    String description,
    String status,
    String priority,
    LocalDateTime dueDate,
    Long assigneeId,
    Long createdByUserId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
}
