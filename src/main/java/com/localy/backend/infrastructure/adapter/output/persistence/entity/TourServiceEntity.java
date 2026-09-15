package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.localy.backend.domain.enums.Category;
import com.localy.backend.domain.enums.ServiceStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "tour_services")
public class TourServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer minParticipants;
    private Integer maxParticipants;
    @ElementCollection
    private List<String>photos;
    private Integer durationMinutes;
    @Enumerated(EnumType.STRING)
    private Category category;
    @Enumerated(EnumType.STRING)
    private ServiceStatus serviceStatus;
    @ElementCollection
    @CollectionTable(name = "tour_service_features", joinColumns = @JoinColumn(name="tour_service_id"))
    private List<ServiceFeatureEntity> features;
    @Embedded
    private LocationEntity meetingLocation;
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private GuideEntity guideEntity;

    @OneToMany(mappedBy = "tourServiceEntity")
    private List<TourScheduleEntity> schedules;
}