package com.localy.backend.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.GuideEntity;

@Mapper(componentModel = "spring" , uses = UserPersistenceMapper.class)
public interface GuidePersistenceMapper {
    @Mapping(target = "tourServiceEntities", ignore = true)
    GuideEntity toEntity (GuideProfile guideProfile);
    GuideProfile toDomain(GuideEntity guideEntity);   
}