package com.securetask.DTO.responses;

public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path
    ) 
{}
