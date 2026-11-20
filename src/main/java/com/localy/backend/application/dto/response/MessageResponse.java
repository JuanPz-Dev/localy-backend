package com.localy.backend.application.dto.response;

import java.time.Instant;

public record MessageResponse(
    Long id,
    Long senderId,
    String senderName,
    String senderProfilePhoto,
    String content,
    boolean read,
    Instant sentAt
){}