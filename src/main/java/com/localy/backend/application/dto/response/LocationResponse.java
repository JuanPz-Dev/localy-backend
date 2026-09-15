package com.localy.backend.application.dto.response;

public record LocationResponse(
    String city,
    String meetingAddress,
    Double latitude,
    Double longitude
){}