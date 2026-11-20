package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.localy.backend.domain.enums.Category;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.TourServiceEntity;

public interface TourJpaRepository extends JpaRepository<TourServiceEntity,Long>{
    @Override
    @EntityGraph(attributePaths = {"guideEntity", "guideEntity.user"})
    java.util.Optional<TourServiceEntity> findById(Long id);

    @EntityGraph(attributePaths = {"guideEntity", "guideEntity.user"})
    Page<TourServiceEntity> findByCategory (Category category,Pageable pageable);

    @EntityGraph(attributePaths = {"guideEntity", "guideEntity.user"})
    Page<TourServiceEntity> findByMeetingLocationCity (String city,Pageable pageable);

    @EntityGraph(attributePaths = {"guideEntity", "guideEntity.user"})
    Page<TourServiceEntity> findByGuideEntity_Id(Long guideId, Pageable pageable);

    @EntityGraph(attributePaths = {"guideEntity", "guideEntity.user"})
    Page<TourServiceEntity> findByServiceStatus(com.localy.backend.domain.enums.ServiceStatus serviceStatus, Pageable pageable);

    @EntityGraph(attributePaths = {"guideEntity", "guideEntity.user"})
    Page<TourServiceEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<TourServiceEntity>findByDescriptionContaining(String text);

    @Query("""
            select case when count(t) > 0 then true else false end
            from TourServiceEntity t join t.photos photo
            where photo = :photoUrl and t.id <> :tourId
            """)
    boolean isPhotoUsedByAnotherTour(
            @Param("photoUrl") String photoUrl,
            @Param("tourId") Long tourId);

    @Override
    @EntityGraph(attributePaths = {"guideEntity", "guideEntity.user"})
    Page<TourServiceEntity> findAll(Pageable pageable);

    //modelo predictivo
    @EntityGraph(attributePaths = {"schedules","guideEntity","guideEntity.user"})
    List<TourServiceEntity> findAllByDeletedFalse();
}