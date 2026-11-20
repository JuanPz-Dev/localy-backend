package com.localy.backend.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.localy.backend.application.dto.request.RegisterUserRequest;
import com.localy.backend.application.dto.response.UserResponse;
import com.localy.backend.domain.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profilePhoto" , ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authProvider", ignore = true)
    User toDomain(RegisterUserRequest userRequest);
    UserResponse toResponse(User user);
}