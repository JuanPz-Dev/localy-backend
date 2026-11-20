package com.localy.backend.application.dto.request;

public record GoogleLoginRequest(
    String email,
    String name,
    String lastName
){}