package com.securetask.Mapper;



import org.springframework.stereotype.Component;

import com.securetask.DTO.requests.RegisterRequest;
import com.securetask.DTO.responses.UserResponse;
import com.securetask.Entitity.User;

@Component
public class UserMapper {


    public User toEntityFromRegisterRequest(RegisterRequest request) 
    {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }


    public UserResponse toRegisterResponse(User user) 
    {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .build();
    }

}
