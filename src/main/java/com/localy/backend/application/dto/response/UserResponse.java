package com.localy.backend.application.dto.response;

import java.util.Set;

import com.localy.backend.domain.enums.AuthProvider;
import com.localy.backend.domain.enums.Rol;

public record UserResponse(
    Long id,
    String profilePhoto,
    String name,
    String lastName,
    String email,
    AuthProvider authProvider,
    Set<Rol>roles
){}