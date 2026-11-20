package com.localy.backend.application.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationRequest(
    @NotNull(message = "La fecha de la reserva es obligatoria")
    LocalDate reservationDate,
    @NotNull(message = "El horario es obligatorio")
    LocalTime startTime,
    @NotNull(message = "La cantidad de adultos es obligatoria")
    @Min(value = 1, message = "Debe haber al menos un adulto")
    Integer adults,
    @NotNull(message = "La cantidad de niños es obligatoria")
    @Min(value = 0, message = "La cantidad de niños no puede ser negativa")
    Integer children
){}