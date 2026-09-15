package com.localy.backend.application.dto.response;

import com.localy.backend.domain.enums.NotificationType;

public record NotificationResponse(
    Long id,
    String title,
    String content,
    NotificationType notificationType,
    boolean read,
    String createdAt
){}