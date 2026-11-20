package com.localy.backend.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.localy.backend.domain.enums.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class Reservation {
    private Long id;
    private User user;
    private TourService tourService;
    private TourSchedule tourSchedule;
    private LocalDate reservationDate;
    private Integer adults;
    private Integer children;
    private BigDecimal basePrice;
    private BigDecimal commissionAmount;
    private BigDecimal totalPrice;
    private ReservationStatus reservationStatus;
    private Instant createdAt;
}