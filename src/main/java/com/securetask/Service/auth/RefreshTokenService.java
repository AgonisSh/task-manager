package com.securetask.Service.auth;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.securetask.Entitity.User;
import com.securetask.Entitity.auth.RefreshToken;
import com.securetask.Exception.EntityNotFoundException;
import com.securetask.Exception.RefreshTokenExpiredException;
import com.securetask.Repository.RefreshTokenRepository;
import com.securetask.Repository.UserRepository;

@Service
public class RefreshTokenService {


    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenDurationMs;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private UserRepository userRepository;
    

    public RefreshToken createRefreshToken(String userEmail) 
    {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        // Delete existing token for user (one active refresh token policy)
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    public RefreshToken verifyExpiration(RefreshToken token) 
    {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token expired. Please login again");
        }
        return token;
    }
    
    public Optional<RefreshToken> findByToken(String token) 
    {
        return refreshTokenRepository.findByToken(token);
    }
}


