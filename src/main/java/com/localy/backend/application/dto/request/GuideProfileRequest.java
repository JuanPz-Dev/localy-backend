package com.localy.backend.application.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record GuideProfileRequest (
    @NotBlank @Size(max = 30)
    String phone,
    @NotBlank @Size(max = 160)
    String address,
    @NotBlank @Size(max = 80)
    String license,
    String description,
    String cv,
    @NotEmpty @Size(max = 10)
    List<String> languages
){}