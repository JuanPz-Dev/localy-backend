package com.localy.backend.application.port.input.admin;

import com.localy.backend.application.dto.response.UserResponse;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.Rol;

public interface FindUsersByRoleUseCase {
    PageResult<UserResponse>execute(Rol rol,PaginationRequest paginationRequest);   
}