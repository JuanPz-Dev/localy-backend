package com.localy.backend.application.port.output;

import java.util.Optional;
import java.util.List;
import java.util.Map;

import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.model.Review;

public interface ReviewRepositoryPort {
    Review save (Review review);
    Optional<Review> findById(Long id);
    Optional<Review> findByUserIdAndTourServiceId(Long userId , Long tourServiceId);
    Map<Long, ReviewStatistics> summarizeByTourServiceIds(List<Long> tourServiceIds);
    PageResult<Review>findByRating(Integer rating, PaginationRequest pageRequest);
    PageResult<Review>findByTourService(Long id,PaginationRequest pageRequest);
}