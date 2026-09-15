package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.localy.backend.domain.enums.ReservationStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "reservation")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate reservationDate;
    private Integer adults;
    private Integer children;
    private BigDecimal totalPrice;
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "tour_service_id")
    private TourServiceEntity tourServiceEntity;

    @ManyToOne
    @JoinColumn(name = "tour_schedule_id")
    private TourScheduleEntity tourScheduleEntity;
}