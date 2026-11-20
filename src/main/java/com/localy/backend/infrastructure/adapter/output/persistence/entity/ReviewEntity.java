package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "reviews",uniqueConstraints = {
        @UniqueConstraint(name = "uk_review_user_tour",columnNames = {"user_id", "tour_service_id"})
    },
    indexes = {
        @Index(name = "idx_review_tour", columnList = "tour_service_id"),
        @Index(name = "idx_review_user", columnList = "user_id")
    }
)
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer rating;
    @Column(nullable = false, length = 2000)
    private String comment;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "tour_service_id" , nullable = false)
    private TourServiceEntity tourService;

    @PrePersist
    protected void onCreated(){
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
