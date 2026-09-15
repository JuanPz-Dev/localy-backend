package com.localy.backend.application.port.input.reservation;

import com.localy.backend.application.dto.request.ReservationRequest;
import com.localy.backend.application.dto.response.ReservationResponse;

public interface CreateReservationUseCase {
    ReservationResponse execute(Long tourServiceId,ReservationRequest reservationRequest); 
}