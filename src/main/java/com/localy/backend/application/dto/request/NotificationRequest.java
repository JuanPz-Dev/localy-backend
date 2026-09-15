package com.localy.backend.application.dto.request;

import com.localy.backend.domain.enums.NotificationType;

public record NotificationRequest(
    String title,
    String content,
    NotificationType notificationType
){}