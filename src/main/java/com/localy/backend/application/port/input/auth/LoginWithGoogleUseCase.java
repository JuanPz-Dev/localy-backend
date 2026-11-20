package com.localy.backend.application.port.input.auth;

import com.localy.backend.application.dto.request.GoogleLoginRequest;
import com.localy.backend.application.dto.response.LoginResponse;

public interface LoginWithGoogleUseCase {
    LoginResponse execute(GoogleLoginRequest googleLoginRequest);
}