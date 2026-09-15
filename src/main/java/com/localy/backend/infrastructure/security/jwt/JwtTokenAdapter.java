package com.localy.backend.infrastructure.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.localy.backend.application.port.security.JwtTokenPort;
import com.localy.backend.domain.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenAdapter implements JwtTokenPort{

    @Value("${app.jwt.secret}")
    private String secret;
    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    @Override
    public String generateToken(User user) {
        return Jwts.builder()
        .subject(user.getEmail())
        .claim("roles", user.getRoles())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(getSigningKey())
        .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
        private SecretKey getSigningKey(){
            return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public String getUsernameFromToken(String token) {
            return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
        }
}