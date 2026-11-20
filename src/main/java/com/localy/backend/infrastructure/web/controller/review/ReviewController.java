package com.localy.backend.infrastructure.web.controller.review;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localy.backend.application.dto.request.ReviewRequest;
import com.localy.backend.application.dto.response.ReviewResponse;
import com.localy.backend.application.port.input.review.CreateReviewUseCase;
import com.localy.backend.application.port.input.review.GetTourReviewsUseCase;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor 
public class ReviewController {
    
    private final CreateReviewUseCase createReviewUseCase;
    private final GetTourReviewsUseCase getTourReviewsUseCase;

    @PostMapping("/tour/{tourId}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long tourId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = createReviewUseCase.execute(tourId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<PageResult<ReviewResponse>> getTourReviews(@PathVariable Long tourId, 
        @RequestParam (defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PaginationRequest paginationRequest = new PaginationRequest(page, size);
        PageResult<ReviewResponse> result = getTourReviewsUseCase.execute(tourId, paginationRequest);
        return ResponseEntity.ok(result);
    }
}