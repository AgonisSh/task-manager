package com.securetask.DTO.requests;

import java.time.LocalDateTime;

import com.securetask.Entitity.Task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    String title,

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    String description,

    @NotNull(message = "Status is required") // @NotNull for enums
    Task.StatusEnum status,

    Task.PriorityEnum priority,
    LocalDateTime dueDate,
    Long assigneeId
) {
    
}
