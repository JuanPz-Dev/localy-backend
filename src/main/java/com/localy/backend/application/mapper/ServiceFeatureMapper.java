package com.localy.backend.application.mapper;

import org.mapstruct.Mapper;

import com.localy.backend.application.dto.request.ServiceFeatureRequest;
import com.localy.backend.application.dto.response.ServiceFeatureResponse;
import com.localy.backend.domain.model.ServiceFeature;

@Mapper(componentModel = "spring")
public interface ServiceFeatureMapper {
    ServiceFeature toDomain(ServiceFeatureRequest serviceFeatureRequest);
    ServiceFeatureResponse toResponse(ServiceFeature serviceFeature);
}