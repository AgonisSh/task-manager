package com.securetask.Service.auth;

import lombok.RequiredArgsConstructor;

import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.securetask.DAO.UserDAO;
import com.securetask.DTO.requests.AuthRequest;
import com.securetask.DTO.requests.LogoutRequest;
import com.securetask.DTO.requests.RefreshTokenRequest;
import com.securetask.DTO.requests.RegisterRequest;
import com.securetask.DTO.responses.AuthResponse;
import com.securetask.Entitity.User;
import com.securetask.Entitity.auth.RefreshToken;
import com.securetask.Exception.InvalidTokenException;
import com.securetask.Factory.UserFactory;
import com.securetask.Validator.UserValidator;

import jakarta.transaction.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final AuthUserDetailsService userDetailsService;
    private final UserDAO userDAO;
    private final UserValidator userValidator;
    private final UserFactory userFactory;


    @Transactional
    public AuthResponse authenticate(AuthRequest authRequest) 
    {
        var token = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);

        String jwtToken = jwtTokenService.generateToken(authentication);
        Long expiresAt = jwtTokenService.extractExpirationTime(jwtToken);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getEmail());

        return new AuthResponse(jwtToken, refreshToken.getToken(), authentication.getName(), expiresAt);
    }

    // Register new user with default role USER, and return JWT + refresh token
    @Transactional
    public AuthResponse register(RegisterRequest userRequest) 
    {
        userValidator.validateUniqueFields(userRequest);
        User user = userFactory.createUser(userRequest, User.Role.USER);
        userDAO.save(Objects.requireNonNull(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String jwtToken = jwtTokenService.generateToken(new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities())
        );

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        Long expiresAt = jwtTokenService.extractExpirationTime(jwtToken);

        return new AuthResponse(jwtToken, refreshToken.getToken(), user.getEmail(), expiresAt);
    }


    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) 
    {
        RefreshToken oldRefreshToken = refreshTokenService.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        
        refreshTokenService.verifyExpiration(oldRefreshToken);
        User user = oldRefreshToken.getUser();

        // Rotate refresh token: delete old and create new
        refreshTokenService.deleteByToken(request.getRefreshToken());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    
        // Generate new JWT token with same user details and authorities
        String jwtToken = jwtTokenService.generateToken(new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities())
        );

        Long expiresAt = jwtTokenService.extractExpirationTime(jwtToken);

        return new AuthResponse(jwtToken, newRefreshToken.getToken(), user.getEmail(), expiresAt);
    }


    @Transactional
    public void logout(LogoutRequest request) 
    {
        refreshTokenService.deleteByToken(request.getRefreshToken());
    }
}

