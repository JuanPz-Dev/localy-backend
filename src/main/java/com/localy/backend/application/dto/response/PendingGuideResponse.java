package com.localy.backend.application.dto.response;

import java.util.List;

import com.localy.backend.domain.enums.GuideStatus;

public record PendingGuideResponse(
    Long id,
    String profilePhoto,
    String name,
    String lastName,
    String phone,
    String address,
    String license,
    String cv,
    String description,
    List<String> languages,
    GuideStatus guideStatus
) {}