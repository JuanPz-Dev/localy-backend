package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.OneToMany;
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
@Table(name = "tour_schedules", uniqueConstraints = {
                                @UniqueConstraint(name="uk_tour_schedule_day_time",columnNames = {"tour_service_id","day_of_week","start_time"})
                            },
                            indexes = @Index(name="idx_tour_schedule_service",columnList = "tour_service_id"))
public class TourScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week",nullable = false,length = 15)
    private DayOfWeek day;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_service_id",nullable = false)
    private TourServiceEntity tourServiceEntity;

    @OneToMany(mappedBy = "tourScheduleEntity",fetch = FetchType.LAZY)
    private List<ReservationEntity> reservations = new ArrayList<>();
}