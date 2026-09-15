package com.localy.backend.application.dto.request;

public record FileUpload(
    byte [] contend,
    String fileName
) {}