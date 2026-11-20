package com.localy.backend.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.GuideEntity;

@Mapper(componentModel = "spring" , uses = UserPersistenceMapper.class)
public interface GuidePersistenceMapper {
    @Mapping(target = "tourServiceEntities", ignore = true)
    GuideEntity toEntity (GuideProfile guideProfile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "tourServiceEntities", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(GuideProfile guideProfile, @MappingTarget GuideEntity guideEntity);

    GuideProfile toDomain(GuideEntity guideEntity);   
}