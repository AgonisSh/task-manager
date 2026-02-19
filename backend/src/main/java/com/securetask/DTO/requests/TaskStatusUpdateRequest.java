package com.securetask.DTO.requests;

import com.securetask.Entitity.Task;

import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateRequest(
    
    @NotNull(message = "Status is required")
    Task.StatusEnum newStatus
) {}
