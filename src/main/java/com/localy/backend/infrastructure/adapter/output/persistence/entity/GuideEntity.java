package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import com.localy.backend.domain.enums.GuideAvailability;
import com.localy.backend.domain.enums.GuideStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "guides", uniqueConstraints = {@UniqueConstraint(name="uk_guides_user_id",columnNames = "user_id"),
       @UniqueConstraint(name="uk_guides_license",columnNames ="license")},
       indexes = {@Index(name = "idx_guides_status", columnList = "guide_status")})
@Entity
public class GuideEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 10)
    private String phone;
    @Column(nullable = false, length = 100)
    private String address;
    @Column(nullable = false, length = 10)
    private String license;
    @Column(name = "description" , columnDefinition = "TEXT")
    private String description;
    @Column(length = 1000)
    private String cv;
    @Column
    private Double averageRating = 0.0;
    @Column(nullable = false)
    private Integer reviewCount = 0;
    @ElementCollection
    @CollectionTable(name = "guide_languages", joinColumns = @JoinColumn(name= "guide_id"),
                    uniqueConstraints = {@UniqueConstraint(name="uk_guide_languages",columnNames = {"guide_id", "language"}
                    )
                }
            )
    @Column(name = "language" , nullable = false)
    private List<String> languages;
    @Enumerated(EnumType.STRING)
    @Column(name = "guide_availability", nullable = false, length = 30)
    private GuideAvailability guideAvailability;
    @Enumerated(EnumType.STRING)
    @Column(name = "guide_status", nullable = false, length = 30)
    private GuideStatus guideStatus;

    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",nullable = false, unique = true)
    private UserEntity user;

    @OneToMany(mappedBy = "guideEntity", fetch = FetchType.LAZY)
    private List<TourServiceEntity>  tourServiceEntities = new ArrayList<>();

    @Version
    private Long version;

    @PrePersist
    protected void onCreate(){
        if (averageRating == null) {
            averageRating = 0.0;
        }
        if (reviewCount == null) {
            reviewCount = 0;
        }
    }
}