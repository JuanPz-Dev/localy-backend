package com.localy.backend.application.port.input.tour;

import com.localy.backend.application.dto.response.TourServiceResponse;

public interface GetTourServiceUseCase {
    TourServiceResponse execute(Long id);
}