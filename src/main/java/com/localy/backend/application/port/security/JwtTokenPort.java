package com.localy.backend.application.port.security;

import com.localy.backend.domain.model.User;

public interface JwtTokenPort {
    String generateToken(User user);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
}