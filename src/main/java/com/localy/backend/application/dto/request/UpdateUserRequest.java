package com.localy.backend.application.dto.request;

public record UpdateUserRequest(
    String profilePhoto,
    String name,
    String lastName,
    String email,
    String password
) {}