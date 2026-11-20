package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.localy.backend.domain.enums.ReservationStatus;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "reservation", indexes = {
    @Index(name = "idx_reservation_user", columnList = "user_id"),
        @Index(name = "idx_reservation_tour", columnList = "tour_service_id"),
        @Index(name = "idx_reservation_status", columnList = "reservation_status"),
        @Index(name = "idx_reservation_date", columnList = "reservation_date")})
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;
    @Column(nullable = false)
    private Integer adults;
    @Column(nullable = false)
    private Integer children;
    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;
    @Column(name = "commission_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal commissionAmount;
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;
    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false, length = 30)
    private ReservationStatus reservationStatus;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_service_id", nullable = false)
    private TourServiceEntity tourServiceEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_schedule_id", nullable = false)
    private TourScheduleEntity tourScheduleEntity;

    protected void onCreated(){
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}