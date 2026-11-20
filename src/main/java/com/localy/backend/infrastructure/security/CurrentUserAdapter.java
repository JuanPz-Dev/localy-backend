package com.localy.backend.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.application.exception.UnauthenticatedException;
import com.localy.backend.application.exception.UserNotFoundException;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUserAdapter implements CurrentUserPort{
    
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            throw new UnauthenticatedException("Usuario no autenticado");
        }

        String email = authentication.getName();

        return userRepositoryPort.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("Usuario autenticado no encontrado"));
    }
}