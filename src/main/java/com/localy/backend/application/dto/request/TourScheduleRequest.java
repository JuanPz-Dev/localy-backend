package com.localy.backend.application.dto.request;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record TourScheduleRequest(
    DayOfWeek day,
    LocalTime startTime
) {}