package com.localy.backend.application.dto.response;

import java.time.Instant;

public record ReviewResponse(
    Long id,
    String userName,
    String profilePhoto,
    Integer rating,
    String comment,
    Instant createdAt
){}