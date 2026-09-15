package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.localy.backend.infrastructure.adapter.output.persistence.entity.ReservationEntity;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity,Long>{
    Page <ReservationEntity> findByUserId (Long id,Pageable pageable);
    Page<ReservationEntity> findByTourServiceEntity_GuideEntity_Id(Long guideId, Pageable pageable);
    Page<ReservationEntity> findByTourServiceEntity_Id(Long tourServiceId, Pageable pageable);
    Page<ReservationEntity> findByReservationStatus(com.localy.backend.domain.enums.ReservationStatus reservationStatus, Pageable pageable);
}