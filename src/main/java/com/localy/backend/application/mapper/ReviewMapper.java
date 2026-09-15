package com.localy.backend.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.localy.backend.application.dto.request.ReviewRequest;
import com.localy.backend.application.dto.response.ReviewResponse;
import com.localy.backend.domain.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "tourService", ignore = true)
    Review toDomain(ReviewRequest reviewRequest);
    
    @Mapping(target = "profilePhoto" , source = "user.profilePhoto")
    @Mapping(target = "userName" , source = "user.name")
    ReviewResponse toResponse(Review review);
}