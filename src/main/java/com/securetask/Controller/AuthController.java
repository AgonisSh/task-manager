package com.securetask.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.securetask.DTO.requests.AuthRequest;
import com.securetask.DTO.requests.RegisterRequest;
import com.securetask.DTO.responses.AuthResponse;
import com.securetask.DTO.responses.UserResponse;
import com.securetask.Service.auth.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        return authService.authenticate(authRequest);
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterRequest userRequest) 
    {
        return new ResponseEntity<>(authService.register(userRequest), HttpStatus.CREATED);
    }
}
