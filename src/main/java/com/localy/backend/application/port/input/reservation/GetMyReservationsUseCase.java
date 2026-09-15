package com.localy.backend.application.port.input.reservation;

import com.localy.backend.application.dto.response.ReservationResponse;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;

public interface GetMyReservationsUseCase {
    PageResult<ReservationResponse> execute(PaginationRequest pageRequest);
}