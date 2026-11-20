package com.localy.backend.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.localy.backend.domain.enums.PaymentMethod;
import com.localy.backend.domain.enums.PaymentStatus;

public record PaymentResponse(
    Long id,
    Long reservationId,
    BigDecimal amount,
    PaymentStatus paymentStatus,
    PaymentMethod paymentMethod,
    String transactionId,
    Instant createdAt,
    Instant paidAt
){}