package com.localy.backend.application.usecase.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.request.LoginUserRequest;
import com.localy.backend.application.dto.response.LoginResponse;
import com.localy.backend.application.exception.InvalidCredentialsException;
import com.localy.backend.application.port.input.auth.LoginUseCase;
import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.application.port.security.JwtTokenPort;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@Service
public class LoginUserUseCaseImpl implements LoginUseCase{

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenPort jwtTokenPort;

    @Override
    public LoginResponse execute(LoginUserRequest userRequest) {

        User user = userRepositoryPort.findByEmail(userRequest.getEmail())
        .orElseThrow(()-> new InvalidCredentialsException("Correo electrónico o contraseña incorrectos"));

        if (!passwordEncoder.matches(userRequest.getPassword(),user.getPassword())) {
            throw new InvalidCredentialsException("Correo electrónico o contraseña incorrectos");
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