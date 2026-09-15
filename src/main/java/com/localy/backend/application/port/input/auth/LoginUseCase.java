package com.localy.backend.application.port.input.auth;

import com.localy.backend.application.dto.request.LoginUserRequest;
import com.localy.backend.application.dto.response.LoginResponse;

public interface LoginUseCase {
    LoginResponse execute(LoginUserRequest userRequest);
}