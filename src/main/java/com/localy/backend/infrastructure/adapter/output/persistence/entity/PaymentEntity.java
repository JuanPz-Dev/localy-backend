package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;

import com.localy.backend.domain.enums.PaymentMethod;
import com.localy.backend.domain.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments",uniqueConstraints = {
       @UniqueConstraint( name = "uk_payment_transaction_id",columnNames = "transaction_id"),
       @UniqueConstraint( name = "uk_payment_reservation_id",columnNames = "reservation_id")},
       indexes = {@Index(name = "idx_payment_status", columnList = "payment_status")})
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_id", nullable = false, length = 100)
    private String transactionId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant paidAt;
    private Instant scheduledReleaseAt;
    private Instant releasedAt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private ReservationEntity reservation;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
