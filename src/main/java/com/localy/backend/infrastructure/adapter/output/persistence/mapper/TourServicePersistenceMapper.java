package com.localy.backend.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.localy.backend.domain.model.TourService;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.TourServiceEntity;

@Mapper(componentModel = "spring" , 
        uses = {GuidePersistenceMapper.class,
                TourSchedulePersistenceMapper.class,
                ServiceFeaturePersistenceMapper.class,
                LocationPersistenceMapper.class
            })
public interface TourServicePersistenceMapper {
    @Mapping(source = "guideProfile", target = "guideEntity")
    TourServiceEntity toEntity(TourService tourService);
    @Mapping(source = "guideEntity", target = "guideProfile")
    TourService toDomain(TourServiceEntity tourServiceEntity);
}