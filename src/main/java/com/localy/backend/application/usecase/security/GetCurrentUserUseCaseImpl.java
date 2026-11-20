package com.localy.backend.application.usecase.security;

import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.response.UserResponse;
import com.localy.backend.application.mapper.UserMapper;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.application.port.security.GetCurrentUserUseCase;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@Service
public class GetCurrentUserUseCaseImpl implements GetCurrentUserUseCase{
    
    private final CurrentUserPort currentUserPort;
    private final UserMapper userMapper;

    @Override
    public UserResponse execute() {
        User user = currentUserPort.getCurrentUser();
        return userMapper.toResponse(user);
    }
}