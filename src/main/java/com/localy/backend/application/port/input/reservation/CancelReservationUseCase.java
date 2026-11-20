package com.localy.backend.application.port.input.reservation;

import com.localy.backend.application.dto.response.ReservationResponse;

public interface CancelReservationUseCase {
    ReservationResponse execute(Long reservationId);
}