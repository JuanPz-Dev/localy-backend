package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.localy.backend.domain.enums.Category;
import com.localy.backend.domain.enums.ServiceStatus;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "tour_services",
    indexes = {@Index(name="idx_tour_guide", columnList = "guide_id"),
               @Index(name="idx_tour_status", columnList = "service_status"),
               @Index(name="idx_tour_category", columnList = "category")}
            )
public class TourServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    @Column(nullable = false)
    private Integer minParticipants;
    @Column(nullable = false)
    private Integer maxParticipants;
    @ElementCollection
    @CollectionTable(name = "tour_service_photos", joinColumns = @JoinColumn(name="tour_service_id"))
    @Column(name = "photo_url",nullable = false, length = 1000)
    private List<String>photos;
    @Column(nullable = false)
    private Integer durationMinutes;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Category category;
    @Enumerated(EnumType.STRING)
    @Column(name = "service_status", nullable = false, length = 30)
    private ServiceStatus serviceStatus;
    @Column(nullable = false)
    private boolean deleted = false;

    @ElementCollection
    @CollectionTable(name = "tour_service_features", joinColumns = @JoinColumn(name="tour_service_id"))
    @AttributeOverrides(
        {
            @AttributeOverride(name = "name",column = @Column(name="feature_name",nullable = false,length = 100)),
            @AttributeOverride(name = "included",column = @Column(name="is_included",nullable = false))
        }
    )
    private List<ServiceFeatureEntity> features;

    @Embedded
    @AttributeOverrides(
        {
            @AttributeOverride(name = "city", column = @Column(name="meeting_city",nullable = false, length = 100)),
            @AttributeOverride(name = "meetingAddress", column = @Column(name="",nullable = false, length = 250)),
            @AttributeOverride(name = "latitude",column = @Column(name="meeting_latitude")),
            @AttributeOverride(name = "longitude",column = @Column(name="meeting_longitude"))
        }
    )
    private LocationEntity meetingLocation;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guide_id", nullable = false)
    private GuideEntity guideEntity;

    @OneToMany(mappedBy = "tourServiceEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TourScheduleEntity> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "tourService" , fetch = FetchType.LAZY)
    private  List<ReviewEntity> review = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}