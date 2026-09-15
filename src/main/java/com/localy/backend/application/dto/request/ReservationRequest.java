package com.localy.backend.application.dto.request;

import java.time.LocalDate;

public record ReservationRequest(
    LocalDate reservationDate,
    Integer adults,
    Integer children
){}