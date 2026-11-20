package com.localy.backend.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;

import com.localy.backend.domain.model.Location;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.LocationEntity;

@Mapper(componentModel = "spring")
public interface LocationPersistenceMapper {
    LocationEntity toEntity (Location location);
    Location toDomain (LocationEntity locationEntity);
}