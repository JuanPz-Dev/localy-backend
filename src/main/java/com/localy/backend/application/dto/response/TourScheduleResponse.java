package com.localy.backend.application.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record TourScheduleResponse(
    DayOfWeek day,
    LocalTime startTime
){}