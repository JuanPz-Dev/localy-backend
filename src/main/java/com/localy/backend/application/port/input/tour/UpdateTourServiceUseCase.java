package com.localy.backend.application.port.input.tour;

import com.localy.backend.application.dto.request.TourServiceRequest;
import com.localy.backend.application.dto.response.TourServiceResponse;

public interface UpdateTourServiceUseCase {
    TourServiceResponse execute(Long id,TourServiceRequest tourServiceRequest);   
}