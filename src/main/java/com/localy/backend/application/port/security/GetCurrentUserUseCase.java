package com.localy.backend.application.port.security;

import com.localy.backend.application.dto.response.UserResponse;

public interface GetCurrentUserUseCase {
    UserResponse  execute();    
}