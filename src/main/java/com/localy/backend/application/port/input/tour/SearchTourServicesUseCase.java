package com.localy.backend.application.port.input.tour;

import com.localy.backend.application.dto.request.TourServiceFilterRequest;
import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;

public interface SearchTourServicesUseCase {
    PageResult<TourServiceResponse>execute(TourServiceFilterRequest filterRequest, PaginationRequest pageRequest);
}