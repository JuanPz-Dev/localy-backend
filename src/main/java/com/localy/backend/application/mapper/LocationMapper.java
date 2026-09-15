package com.localy.backend.application.mapper;

import org.mapstruct.Mapper;

import com.localy.backend.application.dto.request.LocationRequest;
import com.localy.backend.application.dto.response.LocationResponse;
import com.localy.backend.domain.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location toDomain(LocationRequest locationRequest);
    LocationResponse toResponse(Location location);
}