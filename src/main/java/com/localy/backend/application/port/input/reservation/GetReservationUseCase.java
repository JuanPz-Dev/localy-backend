package com.localy.backend.application.port.input.reservation;

import com.localy.backend.application.dto.response.ReservationResponse;

public interface GetReservationUseCase {
    ReservationResponse execute(Long id);
}