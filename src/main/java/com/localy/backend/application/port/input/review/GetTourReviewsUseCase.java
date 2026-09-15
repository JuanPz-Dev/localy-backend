package com.localy.backend.application.port.input.review;

import com.localy.backend.application.dto.response.ReviewResponse;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;

public interface GetTourReviewsUseCase {
    PageResult<ReviewResponse> execute(Long tourId, PaginationRequest pageRequest);
}