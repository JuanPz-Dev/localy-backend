package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.localy.backend.infrastructure.adapter.output.persistence.entity.ReviewEntity;

public interface ReviewJpaRepository extends JpaRepository<ReviewEntity,Long>{
    Page<ReviewEntity> findByTourServiceId(Long id,Pageable pageable);
    Optional<ReviewEntity> findByUser_IdAndTourService_Id(Long userId, Long tourServiceId);
    boolean existsByUser_IdAndTourService_Id(Long userId, Long tourServiceId);

    @Query("""
        SELECT COUNT(review) AS reviewCount, AVG(review.rating) AS averageRating
            FROM ReviewEntity review
            WHERE review.tourService.guideEntity.id = :guideId
            """)
    ReviewStatistics summarizeByGuideId(@Param("guideId") Long guideId);

    @Query("""
        SELECT COUNT(review) AS reviewCount,
               AVG(review.rating) AS averageRating
        FROM ReviewEntity review
        WHERE review.tourService.id = :tourServiceId
        """)
    ReviewStatistics summarizeByTourServiceId(@Param("tourServiceId") Long tourServiceId);

    @Query("""
        SELECT review.tourService.id AS tourServiceId,
               COUNT(review) AS reviewCount,
               AVG(review.rating) AS averageRating
        FROM ReviewEntity review
        WHERE review.tourService.id IN :tourServiceIds
        GROUP BY review.tourService.id
        """)
    List<TourServiceReviewStatistics> summarizeByTourServiceIds(
            @Param("tourServiceIds") Collection<Long> tourServiceIds);

    interface ReviewStatistics {
        Long getReviewCount();
        Double getAverageRating();
    }

    interface TourServiceReviewStatistics extends ReviewStatistics {
        Long getTourServiceId();
    }
}