package com.securetask.Service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.securetask.DAO.UserDAO;
import com.securetask.DTO.requests.AuthRequest;
import com.securetask.DTO.requests.RegisterRequest;
import com.securetask.DTO.responses.AuthResponse;
import com.securetask.DTO.responses.UserResponse;
import com.securetask.Entitity.User;
import com.securetask.Factory.UserFactory;
import com.securetask.Mapper.UserMapper;
import com.securetask.Validator.UserValidator;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserDAO userDAO;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final UserFactory userFactory;


    public AuthResponse authenticate(AuthRequest authRequest) 
    {
        var token = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);

        String jwtToken = jwtTokenService.generateToken(authentication);
        Long expiresAt = jwtTokenService.extractExpirationTime(jwtToken);

        return new AuthResponse(jwtToken, authentication.getName(), expiresAt);
    }


    public UserResponse register(RegisterRequest userRequest) 
    {
        userValidator.validateUniqueFields(userRequest);
        User user = userFactory.createUser(userRequest, User.Role.USER);

        return userMapper.toRegisterResponse(userDAO.save(user));
    }
}
