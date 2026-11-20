package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.localy.backend.application.port.output.ReviewStatistics;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.domain.model.Review;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.GuideEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.ReviewEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.TourServiceEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.mapper.ReviewPersistenceMapper;

class ReviewPersistenceAdapterTest {

    @Test
    void updatesGlobalGuideStatisticsAndKeepsReviewLinkedToTourService() {
        ReviewJpaRepository reviewJpaRepository = mock(ReviewJpaRepository.class);
        GuideJpaRepository guideJpaRepository = mock(GuideJpaRepository.class);
        TourJpaRepository tourJpaRepository = mock(TourJpaRepository.class);
        ReviewPersistenceMapper reviewPersistenceMapper = mock(ReviewPersistenceMapper.class);
        ReviewPersistenceAdapter adapter = new ReviewPersistenceAdapter(
                reviewJpaRepository, reviewPersistenceMapper, guideJpaRepository, tourJpaRepository);

        Long guideId = 8L;
        Long tourServiceId = 20L;
        GuideProfile guideProfile = GuideProfile.builder().id(guideId).build();
        TourService tour = TourService.builder().id(tourServiceId).guideProfile(guideProfile).build();
        Review review = new Review();
        review.setTourService(tour);
        ReviewEntity entity = new ReviewEntity();
        TourServiceEntity persistedTourService = new TourServiceEntity();
        Review mappedReview = new Review();
        GuideEntity guide = new GuideEntity();
        ReviewJpaRepository.ReviewStatistics statistics =
                new ReviewJpaRepository.ReviewStatistics() {
                    @Override
                    public Long getReviewCount() {
                        return 3L;
                    }

                    @Override
                    public Double getAverageRating() {
                        return 4.0;
                    }
                };

        when(reviewPersistenceMapper.toEntity(review)).thenReturn(entity);
        when(tourJpaRepository.getReferenceById(tourServiceId)).thenReturn(persistedTourService);
        when(reviewJpaRepository.save(entity)).thenReturn(entity);
        when(reviewJpaRepository.summarizeByGuideId(guideId)).thenReturn(statistics);
        when(guideJpaRepository.findById(guideId)).thenReturn(Optional.of(guide));
        when(reviewPersistenceMapper.toDomain(entity)).thenReturn(mappedReview);

        Review result = adapter.save(review);

        assertSame(mappedReview, result);
        assertSame(persistedTourService, entity.getTourService());
        assertEquals(4.0, guide.getAverageRating());
        assertEquals(3, guide.getReviewCount());
        verify(reviewJpaRepository).flush();
        verify(reviewJpaRepository).summarizeByGuideId(guideId);
        verify(guideJpaRepository).save(guide);
    }

    @Test
    void returnsStatisticsByTourService() {
        ReviewJpaRepository reviewJpaRepository = mock(ReviewJpaRepository.class);
        ReviewPersistenceAdapter adapter = new ReviewPersistenceAdapter(
                reviewJpaRepository,
                mock(ReviewPersistenceMapper.class),
                mock(GuideJpaRepository.class),
                mock(TourJpaRepository.class));
        ReviewJpaRepository.TourServiceReviewStatistics row =
                mock(ReviewJpaRepository.TourServiceReviewStatistics.class);
        when(row.getTourServiceId()).thenReturn(20L);
        when(row.getReviewCount()).thenReturn(2L);
        when(row.getAverageRating()).thenReturn(4.5);
        when(reviewJpaRepository.summarizeByTourServiceIds(List.of(20L)))
                .thenReturn(List.of(row));

        var statistics = adapter.summarizeByTourServiceIds(List.of(20L));

        assertEquals(new ReviewStatistics(2L, 4.5), statistics.get(20L));
    }
}
