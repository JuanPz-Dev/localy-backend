package com.localy.backend.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.localy.backend.domain.enums.PaymentMethod;
import com.localy.backend.domain.enums.PaymentStatus;

public record PaymentResponse(
    Long id,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    PaymentStatus paymentStatus,
    String transactionId,
    Instant paidAt
){}