package com.localy.backend.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.localy.backend.application.dto.request.GuideProfileRequest;
import com.localy.backend.application.dto.response.GuideProfileResponse;
import com.localy.backend.application.dto.response.PendingGuideResponse;
import com.localy.backend.domain.model.GuideProfile;

@Mapper(componentModel = "spring")
public interface GuideProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "guideStatus", ignore = true)
    @Mapping(target = "guideAvailability", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    GuideProfile toDomain(GuideProfileRequest guideRequest);

    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "profilePhoto", source = "user.profilePhoto")
    GuideProfileResponse toResponse(GuideProfile guideProfile);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "profilePhoto", source = "user.profilePhoto")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "license", source = "license")
    @Mapping(target = "cv", source = "cv")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "languages", source = "languages")
    @Mapping(target = "guideStatus", source = "guideStatus")
    PendingGuideResponse toPendingResponse(GuideProfile guideProfile);
}