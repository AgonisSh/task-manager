package com.securetask.DTO.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters") 
    String title,
    
    @Size(max = 5000, message = "Description must be at most 5000 characters")
    String description,


    
    Long assigneeId
) {
    
}
