package com.localy.backend.application.dto.response;

import java.time.Instant;

public record ConversationResponse(
    Long id,
    Long tourId,
    String tourTitle,
    String otherUserName,
    String otherUserProfilePhoto,
    String lastMessage,
    Instant lastMessageAt,
    Integer unreadMessages,
    Instant createdAt
){}