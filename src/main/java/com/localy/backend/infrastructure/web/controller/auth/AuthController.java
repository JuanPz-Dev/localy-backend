package com.localy.backend.infrastructure.web.controller.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localy.backend.application.dto.request.LoginUserRequest;
import com.localy.backend.application.dto.request.RegisterUserRequest;
import com.localy.backend.application.dto.response.LoginResponse;
import com.localy.backend.application.dto.response.UserResponse;
import com.localy.backend.application.port.input.auth.LoginUseCase;
import com.localy.backend.application.port.input.auth.RegisterUserUseCase;
import com.localy.backend.application.port.security.GetCurrentUserUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<UserResponse>register(@Valid @RequestBody RegisterUserRequest userRequest) {
        UserResponse userResponse = registerUserUseCase.execute(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginUserRequest loginUserRequest) {
        LoginResponse loginResponse = loginUseCase.execute(loginUserRequest);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        return getCurrentUserUseCase.execute();
    }
    
}