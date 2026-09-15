package com.localy.backend.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.localy.backend.domain.enums.Category;
import com.localy.backend.domain.enums.ServiceStatus;

public record TourServiceResponse(
    Long id,
    String title,
    String description,
    List<String>photos,
    BigDecimal price,
    Integer minParticipants,
    Integer maxParticipants,
    Integer durationMinutes,
    Category category,
    ServiceStatus serviceStatus,
    String guideName,
    String guideProfilePicture,
    List<TourScheduleResponse>schedules,
    List<ServiceFeatureResponse>features,
    LocationResponse meetingLocation,
    Double averageRating,
    Integer reviewCount
){}