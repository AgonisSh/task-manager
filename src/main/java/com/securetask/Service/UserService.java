package com.securetask.Service;

import com.securetask.DTO.requests.RegisterRequest;
import com.securetask.DTO.responses.UserResponse;

public interface UserService {

    public UserResponse register(RegisterRequest userRequest);
    
}
