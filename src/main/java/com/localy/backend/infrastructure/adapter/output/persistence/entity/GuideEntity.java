package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import com.localy.backend.domain.enums.GuideAvailability;
import com.localy.backend.domain.enums.GuideStatus;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "guides")
@Entity
public class GuideEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phone;
    private String address;
    private String license;
    @Column(name = "description" , columnDefinition = "TEXT")
    private String description;
    private String cv;
    private Double averageRating;
    private Integer reviewCount;
    @ElementCollection
    private List<String> languages;
    @Enumerated(EnumType.STRING)
    private GuideAvailability guideAvailability;
    @Enumerated(EnumType.STRING)
    private GuideStatus guideStatus;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "guideEntity")
    private List<TourServiceEntity>  tourServiceEntities = new ArrayList<>();
}