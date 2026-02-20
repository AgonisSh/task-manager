package com.securetask.Service.auth;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    @Value("${spring.application.name}")
    private String issuer;
    
    @Value("${jwt.expiration}")
    private Long expiration;

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public String generateToken(Authentication authentication) 
    {
        String scope = "ROLE_USER";
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(this.issuer)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(this.expiration, ChronoUnit.MILLIS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        var encoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return this.encoder.encode(encoderParameters).getTokenValue();
    }

    public Long extractExpirationTime(String token) 
    {
        Jwt jwt = decoder.decode(token);
        var exp = (Instant) jwt.getClaim("exp");
        return exp.toEpochMilli();
    }
}
