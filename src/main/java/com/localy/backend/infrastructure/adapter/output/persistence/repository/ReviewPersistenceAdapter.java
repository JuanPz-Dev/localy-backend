package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.port.output.ReviewRepositoryPort;
import com.localy.backend.application.port.output.ReviewStatistics;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.model.Review;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.GuideEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.ReviewEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.mapper.ReviewPersistenceMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewPersistenceAdapter implements ReviewRepositoryPort{

    private final ReviewJpaRepository reviewJpaRepository;
    private final ReviewPersistenceMapper reviewPersistenceMapper;
    private final GuideJpaRepository guideJpaRepository;
    private final TourJpaRepository tourJpaRepository;

    @Override
    @Transactional
    public Review save(Review review) {
        ReviewEntity entity = reviewPersistenceMapper.toEntity(review);
        Long tourServiceId = review.getTourService().getId();
        entity.setTourService(tourJpaRepository.getReferenceById(tourServiceId));
        ReviewEntity savedReview = reviewJpaRepository.save(entity);
        reviewJpaRepository.flush();
        Long guideId = review.getTourService().getGuideProfile().getId();
        ReviewJpaRepository.ReviewStatistics statistics = reviewJpaRepository.summarizeByGuideId(guideId);
        GuideEntity guide = guideJpaRepository.findById(guideId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el perfil de guía con ID: " + guideId));
        guide.setAverageRating(statistics.getAverageRating());
        guide.setReviewCount(Math.toIntExact(statistics.getReviewCount()));
        guideJpaRepository.save(guide);

        return reviewPersistenceMapper.toDomain(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, ReviewStatistics> summarizeByTourServiceIds(List<Long> tourServiceIds) {
        if (tourServiceIds.isEmpty()) {
            return Map.of();
        }

        return reviewJpaRepository.summarizeByTourServiceIds(tourServiceIds).stream()
                .collect(Collectors.toMap(
                        ReviewJpaRepository.TourServiceReviewStatistics::getTourServiceId,
                        statistics -> new ReviewStatistics(
                                statistics.getReviewCount(),
                                statistics.getAverageRating())));
    }

    @Override
    public Optional<Review> findById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public Optional<Review> findByUserIdAndTourServiceId(Long userId, Long tourServiceId) {
        return reviewJpaRepository.findByUser_IdAndTourService_Id(userId, tourServiceId)
                .map(reviewPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<Review> findByRating(Integer rating, PaginationRequest pageRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByRating'");
    }

    @Override
    public PageResult<Review> findByTourService(Long id,PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page<ReviewEntity> page = reviewJpaRepository.findByTourServiceId(id,pageable);
        return new PageResult<>(page.getContent().stream().map(reviewPersistenceMapper::toDomain).toList(),
        page.getNumber(),page.getSize(), page.getTotalElements());
    }
}