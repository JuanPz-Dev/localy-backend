package com.localy.backend.application.port.output;

import java.util.Optional;

import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.Rol;
import com.localy.backend.domain.model.User;

public interface UserRepositoryPort {
    User save (User user);
    Optional<User>findById(Long id);
    Optional<User>findByEmail(String email);
    boolean existsByEmail(String email);
    PageResult<User>findByRole(Rol rol, PaginationRequest pageRequest);
}