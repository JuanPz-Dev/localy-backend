package com.localy.backend.application.port.input.user;

import com.localy.backend.application.dto.request.UpdateUserRequest;
import com.localy.backend.application.dto.response.UserResponse;

public interface UpdateUserProfileUseCase {
    UserResponse execute(UpdateUserRequest userRequest);
}