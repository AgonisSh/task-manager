package com.securetask.Factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.securetask.DTO.requests.RegisterRequest;
import com.securetask.Entitity.User;
import com.securetask.Entitity.User.Role;

@Component
public class UserFactory {
    
    @Autowired
    private PasswordEncoder encoder;
    
    public User createUser(RegisterRequest request, Role role) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(role)
                .build();
    }

}
