package com.localy.backend.application.usecase.tour;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.localy.backend.application.dto.request.TourServiceFilterRequest;
import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.application.mapper.TourServiceMapper;
import com.localy.backend.application.port.output.ReviewRepositoryPort;
import com.localy.backend.application.port.output.ReviewStatistics;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.model.TourService;

class TourReviewStatisticsUseCasesTest {

    @Test
    void searchMapsTourWithItsOwnReviewStatistics() {
        TourServiceRepositoryPort tourRepository = mock(TourServiceRepositoryPort.class);
        ReviewRepositoryPort reviewRepository = mock(ReviewRepositoryPort.class);
        TourServiceMapper mapper = mock(TourServiceMapper.class);
        SearchTourServicesUseCaseImpl useCase =
                new SearchTourServicesUseCaseImpl(tourRepository, mapper, reviewRepository);
        TourService tour = TourService.builder().id(10L).build();
        TourServiceResponse response = mock(TourServiceResponse.class);
        when(tourRepository.findAll(any(PaginationRequest.class)))
                .thenReturn(new PageResult<>(List.of(tour), 0, 10, 1));
        when(reviewRepository.summarizeByTourServiceIds(List.of(10L)))
                .thenReturn(Map.of(10L, new ReviewStatistics(2L, 4.5)));
        when(mapper.toResponse(tour, 4.5, 2)).thenReturn(response);

        var result = useCase.execute(
                new TourServiceFilterRequest(null, null),
                new PaginationRequest(0, 10));

        assertSame(response, result.getContent().get(0));
        verify(mapper).toResponse(tour, 4.5, 2);
    }

    @Test
    void detailReturnsNullAverageAndZeroReviewsWhenTourHasNoReviews() {
        TourServiceRepositoryPort tourRepository = mock(TourServiceRepositoryPort.class);
        ReviewRepositoryPort reviewRepository = mock(ReviewRepositoryPort.class);
        TourServiceMapper mapper = mock(TourServiceMapper.class);
        GetTourServiceByIdUseCaseImpl useCase =
                new GetTourServiceByIdUseCaseImpl(tourRepository, mapper, reviewRepository);
        TourService tour = TourService.builder().id(10L).build();
        TourServiceResponse response = mock(TourServiceResponse.class);
        when(tourRepository.findById(10L)).thenReturn(Optional.of(tour));
        when(reviewRepository.summarizeByTourServiceIds(List.of(10L))).thenReturn(Map.of());
        when(mapper.toResponse(tour, null, 0)).thenReturn(response);

        assertSame(response, useCase.execute(10L));

        verify(mapper).toResponse(tour, null, 0);
    }
}
