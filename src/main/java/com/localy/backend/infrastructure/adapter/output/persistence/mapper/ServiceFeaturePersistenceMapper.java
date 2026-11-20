package com.localy.backend.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;

import com.localy.backend.domain.model.ServiceFeature;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.ServiceFeatureEntity;

@Mapper(componentModel = "spring")
public interface ServiceFeaturePersistenceMapper {
    ServiceFeatureEntity toEntity (ServiceFeature serviceFeature);
    ServiceFeature toDomain (ServiceFeatureEntity serviceFeatureEntity);
}