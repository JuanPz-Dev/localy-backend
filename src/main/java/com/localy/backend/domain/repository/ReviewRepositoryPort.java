package com.localy.backend.domain.repository;

import java.util.Optional;

import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.model.Review;

public interface ReviewRepositoryPort {
    Review save (Review review);
    Optional<Review> findById(Long id);
    Optional<Review> findByUserIdAndTourServiceId(Long userId , Long tourServiceId);
    PageResult<Review>findByRating(Integer rating, PaginationRequest pageRequest);
    PageResult<Review>findByTourService(Long id, PaginationRequest pageRequest);
}