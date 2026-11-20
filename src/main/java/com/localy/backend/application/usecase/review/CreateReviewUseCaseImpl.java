package com.localy.backend.application.usecase.review;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.request.ReviewRequest;
import com.localy.backend.application.dto.response.ReviewResponse;
import com.localy.backend.application.exception.BusinessRuleException;
import com.localy.backend.application.mapper.ReviewMapper;
import com.localy.backend.application.port.input.review.CreateReviewUseCase;
import com.localy.backend.application.port.output.ReservationRepositoryPort;
import com.localy.backend.application.port.output.ReviewRepositoryPort;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.model.Review;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.domain.model.User;
import com.localy.backend.domain.enums.ReservationStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class CreateReviewUseCaseImpl implements CreateReviewUseCase{

    private final ReviewMapper reviewMapper;
    private final CurrentUserPort currentUserPort;
    private final TourServiceRepositoryPort tourServiceRepositoryPort;
    private final ReviewRepositoryPort reviewRepositoryPort;
    private final ReservationRepositoryPort reservationRepositoryPort;

    @Override
    @Transactional
    public ReviewResponse execute(Long tourId, ReviewRequest reviewRequest) {
        if (reviewRequest == null) {
            throw new BusinessRuleException("La reseña es obligatoria");
        }

        User user = currentUserPort.getCurrentUser();
        TourService tourService = tourServiceRepositoryPort.findById(tourId)
        .orElseThrow(() -> new BusinessRuleException("El servicio no existe"));

        Integer rating = reviewRequest.rating();
        if (rating == null || rating < 1 || rating > 5) {
            throw new BusinessRuleException("La calificación debe estar entre 1 y 5");
        }

        if (reviewRepositoryPort.findByUserIdAndTourServiceId(user.getId(), tourId).isPresent()) {
            throw new BusinessRuleException("Ya publicaste una reseña para este servicio");
        }

        boolean eligibleReservation = reservationRepositoryPort.existsByUserIdAndTourServiceIdAndStatusIn(
                user.getId(), tourId, List.of(ReservationStatus.PAID, ReservationStatus.COMPLETED));
        if (!eligibleReservation) {
            throw new BusinessRuleException("Solo puedes reseñar un servicio con una reserva pagada o completada");
        }

        Review review = reviewMapper.toDomain(reviewRequest);
        review.setUser(user);
        review.setTourService(tourService);
        review.setCreatedAt(Instant.now());

        Review savedReview = reviewRepositoryPort.save(review);
        return reviewMapper.toResponse(savedReview);
    }
}