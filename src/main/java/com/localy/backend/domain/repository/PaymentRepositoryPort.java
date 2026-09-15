package com.localy.backend.domain.repository;

import java.time.Instant;
import java.util.Optional;

import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.PaymentStatus;
import com.localy.backend.domain.model.Payment;

public interface PaymentRepositoryPort {
    Payment save (Payment payment);
    Optional<Payment>findByReservationId(Long reservationId);
    Optional<Payment>findById(Long id);
    PageResult<Payment>findByUser(Long userId, PaginationRequest pageRequest);
    PageResult<Payment>findByGuide(Long guideId, PaginationRequest pageRequest);
    PageResult<Payment>findByStatus(PaymentStatus paymentStatus, PaginationRequest pageRequest);
    PageResult<Payment>findPaymentsToRelease(Instant now, PaginationRequest pageRequest);
}