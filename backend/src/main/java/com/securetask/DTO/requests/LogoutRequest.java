package com.securetask.DTO.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {

    @NotBlank(message = "Refresh token must not be blank")
    private String refreshToken;
    
}
