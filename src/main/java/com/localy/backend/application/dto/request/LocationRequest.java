package com.localy.backend.application.dto.request;

public record LocationRequest (
    String city,
    String meetingAddress,
    Double latitude,
    Double longitude
){}