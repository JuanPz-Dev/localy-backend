package com.localy.backend.application.port.input.tour;

import java.util.List;

import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.domain.common.PaginationRequest;

public interface GetMyTourServicesUseCase {
    List<TourServiceResponse> getMyTourServices(PaginationRequest pageRequest);
}