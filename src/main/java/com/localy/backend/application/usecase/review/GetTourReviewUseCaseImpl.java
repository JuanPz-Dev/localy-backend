package com.localy.backend.application.usecase.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.response.ReviewResponse;
import com.localy.backend.application.mapper.ReviewMapper;
import com.localy.backend.application.port.input.review.GetTourReviewsUseCase;
import com.localy.backend.application.port.output.ReviewRepositoryPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.model.Review;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetTourReviewUseCaseImpl implements GetTourReviewsUseCase{
    
    private final ReviewRepositoryPort reviewRepositoryPort;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResult<ReviewResponse> execute(Long tourId, PaginationRequest pageRequest) {
        PageResult<Review> reviews = reviewRepositoryPort.findByTourService(tourId, pageRequest);
        return new PageResult<>(reviews.getContent().stream().map(reviewMapper::toResponse).toList(),
        reviews.getPage(),reviews.getSize(),reviews.getTotalElements());
    }
}