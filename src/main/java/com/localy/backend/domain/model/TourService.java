package com.localy.backend.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.localy.backend.domain.enums.Category;
import com.localy.backend.domain.enums.ServiceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

@Builder(toBuilder = true)
public class TourService {
    private Long id;
    private GuideProfile guideProfile;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer minParticipants;
    private Integer maxParticipants;
    @Builder.Default
    private boolean deleted = false;
    private List<String>photos;
    private List<TourSchedule>schedules;
    private Integer durationMinutes;
    private Category category;
    private ServiceStatus serviceStatus;
    private List<ServiceFeature> features;
    private Location meetingLocation;
    private Instant createdAt;
}