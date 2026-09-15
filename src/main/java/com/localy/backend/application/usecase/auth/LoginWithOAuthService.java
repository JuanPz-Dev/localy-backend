package com.localy.backend.application.usecase.auth;

import java.time.Instant;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.request.GoogleLoginRequest;
import com.localy.backend.application.dto.response.LoginResponse;
import com.localy.backend.application.exception.InvalidCredentialsException;
import com.localy.backend.application.port.input.auth.LoginWithGoogleUseCase;
import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.application.port.security.JwtTokenPort;
import com.localy.backend.domain.enums.AuthProvider;
import com.localy.backend.domain.enums.Rol;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@Service
public class LoginWithOAuthService implements LoginWithGoogleUseCase{
    
    private final UserRepositoryPort userRepositoryPort;
    private final JwtTokenPort jwtTokenPort;

    @Override
    public LoginResponse execute(GoogleLoginRequest googleLoginRequest) {

        User user = userRepositoryPort.findByEmail(googleLoginRequest.email())
        .orElseGet(()->{
            User newUser = User.builder()
            .name(googleLoginRequest.name())
            .lastName(googleLoginRequest.lastName())
            .email(googleLoginRequest.email())
            .roles(Set.of(Rol.USER))
            .authProvider(AuthProvider.GOOGLE)
            .createdAt(Instant.now())
            .build();

            return userRepositoryPort.save(newUser);
        });
        if (user.getAuthProvider() != AuthProvider.GOOGLE ) {
            throw new InvalidCredentialsException("Esta cuenta no está registrada con Google");
        }
        String accessToken = jwtTokenPort.generateToken(user);
        return new LoginResponse(
            user.getId(),
            user.getProfilePhoto(),
            user.getName(),
            user.getLastName(),
            user.getEmail(),
            user.getAuthProvider(),
            user.getRoles(),
            accessToken,
            "Bearer",
            7200000
        );
    }
}