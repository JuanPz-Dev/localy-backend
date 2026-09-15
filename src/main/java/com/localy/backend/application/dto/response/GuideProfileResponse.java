package com.localy.backend.application.dto.response;

import java.util.List;

import com.localy.backend.domain.enums.GuideAvailability;
import com.localy.backend.domain.enums.GuideStatus;

public record GuideProfileResponse(
    Long id,
    String profilePhoto,
    String name,
    String lastName,
    List<String> languages,
    GuideAvailability guideAvailability,
    GuideStatus guideStatus,
    String phone,
    String address,
    String description,
    Double averageRating,
    Integer reviewCount
){}