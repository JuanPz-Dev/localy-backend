package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import com.localy.backend.domain.enums.ReservationStatus;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.ReservationEntity;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity,Long>{
    Page <ReservationEntity> findByUserId (Long id,Pageable pageable);
    Page<ReservationEntity> findByTourServiceEntity_GuideEntity_Id(Long guideId, Pageable pageable);
    Page<ReservationEntity> findByTourServiceEntity_Id(Long tourServiceId, Pageable pageable);
    Page<ReservationEntity> findByReservationStatus(com.localy.backend.domain.enums.ReservationStatus reservationStatus, Pageable pageable);
    boolean existsByUser_IdAndTourServiceEntity_IdAndReservationStatusIn(Long userId, Long tourServiceId, Collection<ReservationStatus> statuses);
    List<ReservationEntity> findByTourServiceEntity_IdAndReservationDateLessThanEqual(
            Long tourServiceId,
            LocalDate fechaHasta
    );
    List<ReservationEntity> findAllByTourServiceEntity_Id(Long tourServiceId);

    // Modelo predictivo: Trae las reservas de muchos tours en una sola consulta 
    List<ReservationEntity> findAllByTourServiceEntity_IdIn(Collection<Long> tourServiceIds);
}