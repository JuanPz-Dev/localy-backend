package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.PaymentStatus;
import com.localy.backend.domain.model.Payment;
import com.localy.backend.domain.repository.PaymentRepositoryPort;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.PaymentEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.mapper.PaymentPersistenceMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;
    private final PaymentPersistenceMapper paymentPersistenceMapper;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = paymentPersistenceMapper.toEntity(payment);
        entity.setReservation(reservationJpaRepository.getReferenceById(payment.getReservation().getId()));
        return paymentPersistenceMapper.toDomain(paymentJpaRepository.save(entity));
    }

    @Override
    public Optional<Payment> findByReservationId(Long reservationId) {
        return paymentJpaRepository.findByReservation_Id(reservationId)
            .map(paymentPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentJpaRepository.findById(id)
            .map(paymentPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<Payment> findByUser(Long userId, PaginationRequest pageRequest) {
        return toPageResult(paymentJpaRepository.findByReservation_User_Id(userId, pageable(pageRequest)));
    }

    @Override
    public PageResult<Payment> findByGuide(Long guideId, PaginationRequest pageRequest) {
        return toPageResult(paymentJpaRepository.findByReservation_TourServiceEntity_GuideEntity_Id(
            guideId,
            pageable(pageRequest)
        ));
    }

    @Override
    public PageResult<Payment> findByStatus(PaymentStatus paymentStatus, PaginationRequest pageRequest) {
        return toPageResult(paymentJpaRepository.findByPaymentStatus(paymentStatus, pageable(pageRequest)));
    }

    @Override
    public PageResult<Payment> findPaymentsToRelease(Instant now, PaginationRequest pageRequest) {
        return toPageResult(paymentJpaRepository.findByPaymentStatusAndScheduledReleaseAtLessThanEqualAndReleasedAtIsNull(
            PaymentStatus.PAID,
            now,
            pageable(pageRequest)
        ));
    }

    private Pageable pageable(PaginationRequest pageRequest) {
        return PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
    }

    private PageResult<Payment> toPageResult(Page<PaymentEntity> page) {
        List<Payment> payments = page.getContent()
            .stream()
            .map(paymentPersistenceMapper::toDomain)
            .toList();

        return new PageResult<>(payments, page.getNumber(), page.getSize(), page.getTotalElements());
    }
}
