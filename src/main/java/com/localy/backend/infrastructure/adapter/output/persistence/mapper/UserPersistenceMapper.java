package com.localy.backend.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.localy.backend.domain.model.User;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {
    @Mapping(target = "guideEntity" , ignore = true)
    @Mapping(target = "reservations" , ignore = true)
    UserEntity toEntity(User user);
    User toDomain(UserEntity userEntity);
}