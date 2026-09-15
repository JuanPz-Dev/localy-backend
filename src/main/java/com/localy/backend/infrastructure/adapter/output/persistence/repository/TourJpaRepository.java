package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.localy.backend.domain.enums.Category;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.TourServiceEntity;

public interface TourJpaRepository extends JpaRepository<TourServiceEntity,Long>{
    Page<TourServiceEntity> findByCategory (Category category,Pageable pageable);
    Page<TourServiceEntity> findByMeetingLocationCity (String city,Pageable pageable);
    Page<TourServiceEntity> findByGuideEntity_Id(Long guideId, Pageable pageable);
    Page<TourServiceEntity> findByServiceStatus(com.localy.backend.domain.enums.ServiceStatus serviceStatus, Pageable pageable);
    Page<TourServiceEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}