package com.securetask.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.securetask.Entitity.User;
import com.securetask.Entitity.auth.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user);
    
}
