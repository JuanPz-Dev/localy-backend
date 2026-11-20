package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.localy.backend.domain.enums.PaymentStatus;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.PaymentEntity;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByReservation_Id(Long reservationId);
    Page<PaymentEntity> findByReservation_User_Id(Long userId, Pageable pageable);
    Page<PaymentEntity> findByReservation_TourServiceEntity_GuideEntity_Id(Long guideId, Pageable pageable);
    Page<PaymentEntity> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);
    Page<PaymentEntity> findByPaymentStatusAndScheduledReleaseAtLessThanEqualAndReleasedAtIsNull(
        PaymentStatus paymentStatus,
        Instant now,
        Pageable pageable
    );
}
