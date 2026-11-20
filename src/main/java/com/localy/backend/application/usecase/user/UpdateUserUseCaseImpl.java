package com.localy.backend.application.usecase.user;

import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.request.UpdateUserRequest;
import com.localy.backend.application.dto.response.UserResponse;
import com.localy.backend.application.mapper.UserMapper;
import com.localy.backend.application.port.input.user.UpdateUserProfileUseCase;
import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class UpdateUserUseCaseImpl implements UpdateUserProfileUseCase {

    private final CurrentUserPort currentUserPort;
    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    @Override
    public UserResponse execute(UpdateUserRequest updateUserRequest) {
        User user = currentUserPort.getCurrentUser();
        user.setName(updateUserRequest.name());
        user.setLastName(updateUserRequest.lastName());
        user.setEmail(updateUserRequest.email());
        User updatedUser = userRepositoryPort.save(user);
        return userMapper.toResponse(updatedUser);
    }
}