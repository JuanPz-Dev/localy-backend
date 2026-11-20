package com.localy.backend.application.dto.request;

public record ServiceFeatureRequest(
    String name,
    boolean included
) {}