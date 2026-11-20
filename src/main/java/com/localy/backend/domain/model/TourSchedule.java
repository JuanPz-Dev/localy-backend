package com.localy.backend.domain.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class TourSchedule {
    private Long id;
    private DayOfWeek day;
    private LocalTime startTime;
}