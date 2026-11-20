package com.localy.backend.application.usecase.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.response.UserResponse;
import com.localy.backend.application.mapper.UserMapper;
import com.localy.backend.application.port.input.admin.FindUsersByRoleUseCase;
import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.Rol;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class FindUsersByRoleUseCaseImpl implements FindUsersByRoleUseCase{

    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;
    
    @Override
    @Transactional(readOnly = true)
    public PageResult<UserResponse> execute(Rol rol, PaginationRequest paginationRequest) {
        PageResult<User> users = userRepositoryPort.findByRole(rol, paginationRequest);
        return new PageResult<>(
            users.getContent().stream().map(userMapper::toResponse).toList(),
            users.getPage(),
            users.getSize(),
            users.getTotalElements()
        );
    }
}