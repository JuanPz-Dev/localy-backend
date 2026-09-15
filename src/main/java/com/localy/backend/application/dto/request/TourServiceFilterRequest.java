package com.localy.backend.application.dto.request;

import com.localy.backend.domain.enums.Category;

public record TourServiceFilterRequest(
    Category category,
    String city
){}