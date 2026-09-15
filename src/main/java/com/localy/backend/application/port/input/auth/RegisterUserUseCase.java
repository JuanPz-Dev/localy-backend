package com.localy.backend.application.port.input.auth;

import com.localy.backend.application.dto.request.RegisterUserRequest;
import com.localy.backend.application.dto.response.UserResponse;


public interface RegisterUserUseCase {
    UserResponse execute(RegisterUserRequest userRequest);   
}