package com.localy.backend.application.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.localy.backend.domain.enums.Category;

public record TourServiceRequest(
    String title,
    String description,
    BigDecimal price,
    Integer minParticipants,
    Integer maxParticipants,
    //List<String> photos,
    List<TourScheduleRequest> schedules,
    Integer durationMinutes,
    Category category,
    List<ServiceFeatureRequest> features,
    LocationRequest meetingLocation
){}