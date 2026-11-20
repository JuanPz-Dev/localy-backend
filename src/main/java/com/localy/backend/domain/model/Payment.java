package com.localy.backend.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.localy.backend.domain.enums.PaymentMethod;
import com.localy.backend.domain.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class Payment {
    private Long id;
    private Reservation reservation;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String transactionId;

    private Instant createdAt;
    private Instant paidAt;
    private Instant scheduledReleaseAt;
    private Instant releasedAt;
}