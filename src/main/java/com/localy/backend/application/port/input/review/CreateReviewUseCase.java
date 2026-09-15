package com.localy.backend.application.port.input.review;

import com.localy.backend.application.dto.request.ReviewRequest;
import com.localy.backend.application.dto.response.ReviewResponse;

public interface CreateReviewUseCase {
    ReviewResponse execute(Long userId, Long tourId, ReviewRequest reviewRequest);
}