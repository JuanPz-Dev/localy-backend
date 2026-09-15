package com.localy.backend.application.dto.request;

public record ReviewRequest(
    Integer rating,
    String comment
){}