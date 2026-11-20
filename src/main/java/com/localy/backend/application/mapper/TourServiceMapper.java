package com.localy.backend.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.localy.backend.application.dto.request.TourServiceRequest;
import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.domain.model.TourService;

@Mapper(componentModel = "spring" , uses = {
    LocationMapper.class,
    TourScheduleMapper.class,
    ServiceFeatureMapper.class
})
public interface TourServiceMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "guideProfile", ignore = true)
    @Mapping(target = "serviceStatus", ignore = true)
    @Mapping(target = "photos", ignore = true)
    TourService toDomain(TourServiceRequest tourServiceRequest);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "guideProfile", ignore = true)
    @Mapping(target = "serviceStatus", ignore = true)
    @Mapping(target = "photos", ignore = true)
    void updateDomain(TourServiceRequest request,
        @MappingTarget TourService tourService
    );

    @Mapping(target = "guideId", source = "guideProfile.id")
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "guideName", source = "guideProfile.user.name")
    @Mapping(target = "guideProfilePicture", source = "guideProfile.user.profilePhoto")
    TourServiceResponse toResponse(TourService tourService);

    @Mapping(target = "guideId", source = "tourService.guideProfile.id")
    @Mapping(target = "averageRating", source = "averageRating")
    @Mapping(target = "reviewCount", source = "reviewCount")
    @Mapping(target = "guideName", source = "tourService.guideProfile.user.name")
    @Mapping(target = "guideProfilePicture", source = "tourService.guideProfile.user.profilePhoto")
    TourServiceResponse toResponse(
        TourService tourService,
        Double averageRating,
        Integer reviewCount
    );
}