package com.localy.backend.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import com.localy.backend.domain.enums.ReservationStatus;

public record ReservationResponse(
    Long id,
    Long tourId,
    String tourName,
    String guideName,
    String touristName,
    LocalDate reservationDate,
    LocalTime startTime,
    Integer adults,
    Integer children,
    BigDecimal basePrice,
    BigDecimal commissionAmount,
    BigDecimal totalPrice,
    ReservationStatus status,
    Instant createdAt
){}