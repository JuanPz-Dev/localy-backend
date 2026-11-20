package com.localy.backend.application.usecase.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.localy.backend.application.dto.request.ReviewRequest;
import com.localy.backend.application.dto.response.ReviewResponse;
import com.localy.backend.application.exception.BusinessRuleException;
import com.localy.backend.application.mapper.ReviewMapper;
import com.localy.backend.application.port.output.ReservationRepositoryPort;
import com.localy.backend.application.port.output.ReviewRepositoryPort;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.enums.ReservationStatus;
import com.localy.backend.domain.model.Review;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.domain.model.User;

class CreateReviewUseCaseImplTest {

    private static final Long USER_ID = 7L;
    private static final Long TOUR_ID = 12L;

    private CurrentUserPort currentUserPort;
    private TourServiceRepositoryPort tourServiceRepositoryPort;
    private ReviewRepositoryPort reviewRepositoryPort;
    private ReservationRepositoryPort reservationRepositoryPort;
    private ReviewMapper reviewMapper;
    private CreateReviewUseCaseImpl useCase;
    private User user;
    private TourService tour;

    @BeforeEach
    void setUp() {
        currentUserPort = mock(CurrentUserPort.class);
        tourServiceRepositoryPort = mock(TourServiceRepositoryPort.class);
        reviewRepositoryPort = mock(ReviewRepositoryPort.class);
        reservationRepositoryPort = mock(ReservationRepositoryPort.class);
        reviewMapper = mock(ReviewMapper.class);
        useCase = new CreateReviewUseCaseImpl(
                reviewMapper,
                currentUserPort,
                tourServiceRepositoryPort,
                reviewRepositoryPort,
                reservationRepositoryPort);
        user = User.builder().id(USER_ID).name("Turista").build();
        tour = TourService.builder().id(TOUR_ID).title("Centro Histórico").build();

        when(currentUserPort.getCurrentUser()).thenReturn(user);
        when(tourServiceRepositoryPort.findById(TOUR_ID)).thenReturn(Optional.of(tour));
        when(reviewRepositoryPort.findByUserIdAndTourServiceId(USER_ID, TOUR_ID))
                .thenReturn(Optional.empty());
    }

    @Test
    void savesReviewWithAuthenticatedAuthorAndTourWhenReservationIsPaidOrCompleted() {
        ReviewRequest request = new ReviewRequest(5, "Excelente");
        Review mapped = new Review();
        mapped.setRating(5);
        mapped.setComment("Excelente");
        ReviewResponse expected = new ReviewResponse(1L, "Turista", null, 5, "Excelente", Instant.now());
        when(reviewMapper.toDomain(request)).thenReturn(mapped);
        when(reviewRepositoryPort.save(mapped)).thenReturn(mapped);
        when(reviewMapper.toResponse(mapped)).thenReturn(expected);
        when(reservationRepositoryPort.existsByUserIdAndTourServiceIdAndStatusIn(
                eq(USER_ID), eq(TOUR_ID), any()))
                .thenReturn(true);

        ReviewResponse response = useCase.execute(TOUR_ID, request);

        assertSame(expected, response);
        assertSame(user, mapped.getUser());
        assertSame(tour, mapped.getTourService());
        assertEquals(5, mapped.getRating());
        ArgumentCaptor<List<ReservationStatus>> statuses = ArgumentCaptor.forClass(List.class);
        verify(reservationRepositoryPort).existsByUserIdAndTourServiceIdAndStatusIn(
                eq(USER_ID), eq(TOUR_ID), statuses.capture());
        assertEquals(List.of(ReservationStatus.PAID, ReservationStatus.COMPLETED), statuses.getValue());
        verify(reviewRepositoryPort).save(mapped);
    }

    @Test
    void rejectsInvalidRating() {
        assertThrows(BusinessRuleException.class, () -> useCase.execute(TOUR_ID, new ReviewRequest(0, "Mal")));

        verify(reviewRepositoryPort, never()).save(any());
        verify(reservationRepositoryPort, never())
                .existsByUserIdAndTourServiceIdAndStatusIn(any(), any(), any());
    }

    @Test
    void rejectsDuplicateReviewForUserAndTour() {
        when(reviewRepositoryPort.findByUserIdAndTourServiceId(USER_ID, TOUR_ID))
                .thenReturn(Optional.of(new Review()));

        assertThrows(BusinessRuleException.class,
                () -> useCase.execute(TOUR_ID, new ReviewRequest(4, "Otra reseña")));

        verify(reservationRepositoryPort, never())
                .existsByUserIdAndTourServiceIdAndStatusIn(any(), any(), any());
        verify(reviewRepositoryPort, never()).save(any());
    }

    @Test
    void rejectsReviewWithoutPaidOrCompletedReservation() {
        when(reservationRepositoryPort.existsByUserIdAndTourServiceIdAndStatusIn(
                eq(USER_ID), eq(TOUR_ID), any()))
                .thenReturn(false);

        assertThrows(BusinessRuleException.class,
                () -> useCase.execute(TOUR_ID, new ReviewRequest(4, "Aún pendiente")));

        verify(reviewRepositoryPort, never()).save(any());
    }

    @Test
    void rejectsMissingTour() {
        when(tourServiceRepositoryPort.findById(TOUR_ID)).thenReturn(Optional.empty());

        assertThrows(BusinessRuleException.class,
                () -> useCase.execute(TOUR_ID, new ReviewRequest(4, "Reseña")));

        verify(reviewRepositoryPort, never()).save(any());
    }
}
